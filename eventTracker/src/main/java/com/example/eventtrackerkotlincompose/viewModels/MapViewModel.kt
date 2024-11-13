package com.example.eventtrackerkotlincompose.viewModels

import android.app.Application
import android.graphics.drawable.BitmapDrawable
import android.util.Log
import androidx.compose.runtime.mutableStateOf
import androidx.compose.ui.unit.dp
import androidx.core.content.ContextCompat
import androidx.lifecycle.AndroidViewModel
import androidx.lifecycle.viewModelScope
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.geometry.Point
import com.arcgismaps.mapping.symbology.PictureMarkerSymbol
import com.arcgismaps.mapping.view.SingleTapConfirmedEvent
import com.arcgismaps.tasks.geocode.LocatorTask
import com.arcgismaps.toolkit.geoviewcompose.MapViewProxy
import com.example.eventtrackerkotlincompose.R
import com.example.eventtrackerkotlincompose.dataStore.UserDetailsStore
import com.example.eventtrackerkotlincompose.network.Event
import com.example.eventtrackerkotlincompose.network.EventsRepository
import com.example.eventtrackerkotlincompose.network.HttpService
import com.example.eventtrackerkotlincompose.network.NetworkClient
import kotlinx.coroutines.flow.MutableStateFlow
import kotlinx.coroutines.flow.SharingStarted
import kotlinx.coroutines.flow.StateFlow
import kotlinx.coroutines.flow.distinctUntilChanged
import kotlinx.coroutines.flow.stateIn
import kotlinx.coroutines.launch

class MapViewModel(application: Application) : AndroidViewModel(application) {
    private val locatorTask = LocatorTask("https://geocode-api.arcgis.com/arcgis/rest/services/World/GeocodeServer")
    private val _map = MutableStateFlow(createMap())
    val map: StateFlow<ArcGISMap> = _map

    private val _graphicsOverlay = MutableStateFlow(GraphicsOverlay())
    val graphicsOverlay: StateFlow<GraphicsOverlay> = _graphicsOverlay

    private val _events = MutableStateFlow<List<Event>?>(null)
    val events : StateFlow<List<Event>?> = _events
    private val pointEventsMap: HashMap<Point, MutableList<Event>> = HashMap()

    val mapViewProxy = MapViewProxy()
    val showEventBottomSheet = mutableStateOf(false)

    private val apiService = HttpService(NetworkClient.client)
    private val eventsRepository = EventsRepository(apiService)
    private val repository = UserDetailsStore(application)
    private val userToken = repository.getToken
        .distinctUntilChanged()
        .stateIn(viewModelScope, SharingStarted.WhileSubscribed(5000), "")
    init {
        viewModelScope.launch {
            userToken.collect{
                if (!it.isNullOrEmpty()) {
                    _events.value = eventsRepository.getAllEvents(userToken.value!!)
                    Log.d("events", _events.value.toString())
                    val pointsList = mutableListOf<Point>()
                    _events.value?.let{eventsList ->
                        eventsList.forEach { event ->
                            event.location.street
                            Log.d("strada", event.location.city + " " + event.location.street)

                            val geocodeResults = locatorTask.geocode(event.location.city + " " + event.location.street).getOrNull()
                            val firstResult = geocodeResults?.get(0)
                            if (firstResult != null) {
                                firstResult.displayLocation?.let {p ->
                                    addPointToGraphicsOverlay(p)
                                    pointsList.add(p)
                                    if (pointEventsMap.containsKey(p)) {
                                        pointEventsMap[p]?.add(event)
                                    } else {
                                        pointEventsMap[p] = mutableListOf(event)
                                    }
                                }
                            }
                        }
                    }
                }
            }
        }
    }

    private fun createMap(): ArcGISMap {
        return ArcGISMap(BasemapStyle.ArcGISTopographic).apply {
            initialViewpoint = Viewpoint(
                latitude = 44.4268,
                longitude = 26.1025,
                scale = 72000.0
            )
        }
    }
    private fun addPointToGraphicsOverlay(point: Point) {

        val pinDrawable = ContextCompat.getDrawable(getApplication(), R.drawable.pin) as BitmapDrawable
        val pinSymbol = PictureMarkerSymbol.createWithImage(pinDrawable)
        pinSymbol.apply {
            width = 18f
            height = 65f
        }

        val pointGraphic = Graphic(
            geometry = point,
            symbol = pinSymbol
        )

        _graphicsOverlay.value.graphics.add(pointGraphic)
    }

    fun dismissBottomSheet() {
        showEventBottomSheet.value = false
    }

    suspend fun setViewpoint(center: Point) {
        mapViewProxy.setViewpointCenter(center, 10000.0)
    }
    suspend fun identify(singleTapConfirmedEvent: SingleTapConfirmedEvent) {
        dismissBottomSheet()
        val res = mapViewProxy.identifyGraphicsOverlays(screenCoordinate = singleTapConfirmedEvent.screenCoordinate,10.dp,false,1).getOrNull()
        res?.firstOrNull()?.graphics?.firstOrNull()?.geometry?.let { geometry ->
            if (geometry is Point) {
                _events.value = pointEventsMap[geometry]
                showEventBottomSheet.value = true
                setViewpoint(geometry)
                Log.d("asda", _events.toString())
            } else {
                Log.d("Coordinates", "Geometry is not a point")
            }
        } ?: run {
            Log.d("Coordinates", "No results found or no graphics in the first result")
        }
    }
}
