# GoogleMapRipples
Ring and filled Ripples on google maps for android.

[![Android Arsenal](https://img.shields.io/badge/Android%20Arsenal-GoogleMapsRippleEffect-green.svg?style=true)](https://android-arsenal.com/details/1/4126)

# GoogleMapsRippleEffect        

"GoogleMapsRippleEffect" is an awesome first of its type android library for showing a ripple on a google map, e.g show catchment area of an earthquake where ripples have been felt, give prominence to certain markers which need to be highlighted. Also add a ripple when your user is moving on the map and give a #PokemonGo type ripple effect. The example details of the same will be added soon. 

Below samples show the ripple effect in action:

![](https://github.com/premacck/GoogleMapRipples/blob/main/gifs/ripple_ring.gif)                ![](https://github.com/premacck/GoogleMapRipples/blob/main/gifs/ripple_filled.gif)

------    

# Download    

### Using Gradle: under dependencies section:   

```gradle
implementation 'com.github.premacck:GoogleMapRipples:1.0.0'
```

### or Using Maven:
```XML
<repositories>
  <repository>
    <id>jitpack.io</id>
    <url>https://jitpack.io</url>
  </repository>
</repositories>

<dependency>
  <groupId>com.github.premacck</groupId>
  <artifactId>GoogleMapRipples</artifactId>
  <version>1.0.0</version>
</dependency>
```
------

# Documentation

### Default Ripple animation
Just two lines of code :  
Use `startRippleMapAnimation()` and `stopRippleMapAnimation()` methods to start and stop Animation respectively.     
Example is given below (Preview shown above in first sample)

```kotlin
// mMap is GoogleMap object, latLng is the location on map from which ripple should start

MapRipple mapRipple = MapRipple(mMap, latLng, context);
mapRipple.startRippleMapAnimation()                     //in onMapReadyCallBack

override fun onStop() {
  super.onStop()
  if (mapRipple.isAnimationRunning()) {
    mapRipple.stopRippleMapAnimation()
  }
}

// Start Animation again only if it is not running
if (!mapRipple.isAnimationRunning()) {
  mapRipple.startRippleMapAnimation()
}
```

### Advanced Ripple animation

Example is given below (Preview shown above in second sample)

```kotlin
// mMap is GoogleMap object, latLng is the location on map from which ripple should start

mapRipple = new MapRipple(mMap, latLng, context)
mapRipple.withNumberOfRipples(3)
mapRipple.withFillColor(Color.BLUE)
mapRipple.withStrokeColor(Color.BLACK)
mapRipple.withStrokewidth(10)           // 10dp
mapRipple.withDistance(2000)            // 2000 metres radius
mapRipple.withRippleDuration(12000)     //12000ms
mapRipple.withTransparency(0.5f)
mapRipple.startRippleMapAnimation()
// Use same procedure to stop Animation and start it again as mentioned anove in Default Ripple Animation Sample

// New functions:

// the repeat mode of ripple animation, can be ValueAnimator.RESTART or ValueAnimator.REVERSE.
// default value is ValueAnimator.REVERSE
mapRipple.withRepeatMode(ValueAnimator.RESTART)

// If true, ripple will fade out as it expands. Default value is true
withFadingOutRipple(true)
```

### Update center of ripple as location changes(Needed when user moves)
Just one line of code is needed:  
Use `mapRipple.withLatLng(LatLng changedLatlng)` method anytime in future to update center of ripple.     

```kotlin
// after implementing **LocationListener** interface to current class use:
override fun onLocationChanged(Location location) {
  mapRipple.withLatLng(new LatLng(location.latitude, location.longitude))
}
```
------

### Compatibility

**Minimum Android SDK**: This library requires a minimum API level of **16**.    

------
