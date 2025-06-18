package com.on.staccato.presentation.map.cluster

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.on.staccato.presentation.map.model.StaccatoLocationUiModel

class StaccatoMarkerRender(
    context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<StaccatoLocationUiModel>?,
    private val clusterDrawManager: ClusterDrawManager,
) : DefaultClusterRenderer<StaccatoLocationUiModel>(context, map, clusterManager) {
    override fun onBeforeClusterRendered(
        cluster: Cluster<StaccatoLocationUiModel>,
        markerOptions: MarkerOptions,
    ) {
        val icon: BitmapDescriptor = clusterDrawManager.generateClusterIcon(cluster.size)
        markerOptions.icon(icon)
    }

    override fun getDescriptorForCluster(cluster: Cluster<StaccatoLocationUiModel>): BitmapDescriptor =
        clusterDrawManager.generateClusterIcon(
            cluster.size,
        )

    override fun onBeforeClusterItemRendered(
        item: StaccatoLocationUiModel,
        markerOptions: MarkerOptions,
    ) {
        val icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(item.color.markerRes)
        markerOptions.icon(icon)
    }
}
