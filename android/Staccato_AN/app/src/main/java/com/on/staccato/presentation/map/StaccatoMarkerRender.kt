package com.on.staccato.presentation.map

import android.content.Context
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptor
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.MarkerOptions
import com.google.maps.android.clustering.Cluster
import com.google.maps.android.clustering.ClusterManager
import com.google.maps.android.clustering.view.DefaultClusterRenderer
import com.on.staccato.domain.model.StaccatoLocation
import com.on.staccato.presentation.common.color.CategoryColor

class StaccatoMarkerRender(
    context: Context,
    map: GoogleMap?,
    clusterManager: ClusterManager<StaccatoLocation>?,
    private val clusterDrawManager: ClusterDrawManager,
) : DefaultClusterRenderer<StaccatoLocation>(context, map, clusterManager) {
    override fun onBeforeClusterRendered(
        cluster: Cluster<StaccatoLocation>,
        markerOptions: MarkerOptions,
    ) {
        val icon: BitmapDescriptor = clusterDrawManager.generateClusterIcon(cluster.size)
        markerOptions.icon(icon)
    }

    override fun getDescriptorForCluster(cluster: Cluster<StaccatoLocation>): BitmapDescriptor =
        clusterDrawManager.generateClusterIcon(
            cluster.size,
        )

    override fun onBeforeClusterItemRendered(
        item: StaccatoLocation,
        markerOptions: MarkerOptions,
    ) {
        val icon: BitmapDescriptor = BitmapDescriptorFactory.fromResource(CategoryColor.getMarkerResBy(item.color))
        markerOptions.icon(icon)
    }
}
