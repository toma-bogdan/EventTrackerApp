package com.example.eventtrackerkotlincompose

import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.res.stringResource
import com.arcgismaps.mapping.ArcGISMap
import com.arcgismaps.mapping.BasemapStyle
import com.arcgismaps.mapping.Viewpoint
import com.arcgismaps.toolkit.geoviewcompose.MapView
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.padding
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.runtime.remember
import androidx.compose.ui.res.stringResource
import com.arcgismaps.Color
import com.arcgismaps.geometry.Point
import com.arcgismaps.geometry.PolygonBuilder
import com.arcgismaps.geometry.PolylineBuilder
import com.arcgismaps.geometry.SpatialReference
import com.arcgismaps.mapping.symbology.SimpleFillSymbol
import com.arcgismaps.mapping.symbology.SimpleFillSymbolStyle
import com.arcgismaps.mapping.symbology.SimpleLineSymbol
import com.arcgismaps.mapping.symbology.SimpleLineSymbolStyle
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbol
import com.arcgismaps.mapping.symbology.SimpleMarkerSymbolStyle
import com.arcgismaps.mapping.view.Graphic
import com.arcgismaps.mapping.view.GraphicsOverlay
import com.arcgismaps.toolkit.geoviewcompose.MapView

// Create a blue outline symbol.
private val blueOutlineSymbol by lazy {
    SimpleLineSymbol(SimpleLineSymbolStyle.Solid, Color.fromRgba(0, 0, 255), 2f)
}
private val pointGraphic by lazy {
    // Create a point geometry with a location and spatial reference.
    // Point(latitude, longitude, spatial reference)
    val point = Point(
        x = 26.1025,
        y = 44.4268,
        spatialReference = SpatialReference.wgs84()
    )

    // Create a point symbol that is an small red circle and assign the blue outline symbol to its outline property.
    val simpleMarkerSymbol = SimpleMarkerSymbol(
        style = SimpleMarkerSymbolStyle.Circle,
        color = Color.red,
        size = 10f
    )
    simpleMarkerSymbol.outline = blueOutlineSymbol

    Graphic(
        geometry = point,
        symbol = simpleMarkerSymbol
    )
}

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun MapMainScreen() {

    val map = remember {
        createMap()
    }
    // Create a graphics overlay.
    val graphicsOverlay = remember { GraphicsOverlay() }

    // Add the point graphic to the graphics overlay.
    graphicsOverlay.graphics.add(pointGraphic)

    // Create a list of graphics overlays used by the MapView
    val graphicsOverlays = remember { listOf(graphicsOverlay) }



    Scaffold(
        topBar = { TopAppBar(title = { Text(text = stringResource(id = R.string.app_name)) }) }
    ) {

        MapView(
            modifier = Modifier.fillMaxSize().padding(it),
            arcGISMap = map,
            graphicsOverlays = graphicsOverlays
        )

    }

}

fun createMap(): ArcGISMap {

    return ArcGISMap(BasemapStyle.ArcGISTopographic).apply {

        initialViewpoint = Viewpoint(
            latitude = 44.4268,
            longitude = 26.1025,

            scale = 72000.0

        )

    }

}