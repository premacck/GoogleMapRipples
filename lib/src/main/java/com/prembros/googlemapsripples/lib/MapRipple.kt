package com.prembros.googlemapsripples.lib

import android.animation.IntEvaluator
import android.animation.ValueAnimator
import android.content.Context
import android.content.res.Resources
import android.graphics.Bitmap
import android.graphics.Canvas
import android.graphics.Color
import android.graphics.drawable.BitmapDrawable
import android.graphics.drawable.Drawable
import android.graphics.drawable.GradientDrawable
import android.os.Handler
import android.os.Looper
import android.view.animation.DecelerateInterpolator
import androidx.core.content.ContextCompat
import com.google.android.gms.maps.GoogleMap
import com.google.android.gms.maps.model.BitmapDescriptorFactory
import com.google.android.gms.maps.model.GroundOverlay
import com.google.android.gms.maps.model.GroundOverlayOptions
import com.google.android.gms.maps.model.LatLng
import kotlin.math.abs

/**
 * Prem's creation, on 06/03/21
 */
@Suppress("unused", "MemberVisibilityCanBePrivate") class MapRipple(private val googleMap: GoogleMap, latLng: LatLng, context: Context) {

  /** Ripple image */
  private var backgroundImage: Bitmap? = null

  /** Transparency of image */
  private var transparency = 0.5F

  /** Repeat mode of the ripple */
  private var repeatMode: Int = ValueAnimator.RESTART

  /** If true, ripple will fade out as it expands. Default value is true */
  private var isRippleFadeOutEnabled: Boolean = true

  /** Distance to which ripple should be shown in metres */
  @Volatile private var distance = 2000.0

  /** Number of ripples to show, max = 4 */
  private var numberOfRipples = 1

  /** Fill color of circle */
  private var fillColor: Int = Color.TRANSPARENT

  /** Border color of circle */
  private var strokeColor: Int = Color.BLACK

  /** Border width of circle */
  private var strokeWidth = 10

  /** Duration between two ripples in microseconds */
  private var durationBetweenTwoRipples: Long = 4000

  /** Duration of the ripple in microseconds */
  private var rippleDuration: Long = 12000
  private var latLng: LatLng
  private var previousLatLng: LatLng
  private val vAnimators: Array<ValueAnimator?>
  private val handlers: Array<Handler?>
  private val gOverlays: Array<GroundOverlay?>
  private val drawable: GradientDrawable
  var isAnimationRunning = false

  init {
    this.latLng = latLng
    previousLatLng = latLng
    drawable = ContextCompat.getDrawable(context, R.drawable.bg_map_ripple_ring) as GradientDrawable
    vAnimators = arrayOfNulls(4)
    handlers = arrayOfNulls(4)
    gOverlays = arrayOfNulls(4)
  }

  fun withTransparency(transparency: Float) = apply {
    this.transparency = transparency
  }

  fun withDistance(distance: Double) = apply {
    var tempDistance = distance
    if (tempDistance < 200) tempDistance = 200.0
    this.distance = tempDistance
  }

  fun withLatLng(latLng: LatLng) = apply {
    previousLatLng = this.latLng
    this.latLng = latLng
  }

  fun withNumberOfRipples(numberOfRipples: Int) = apply {
    var rippleCount = numberOfRipples
    if (rippleCount > 4 || rippleCount < 1) rippleCount = 4
    this.numberOfRipples = rippleCount
  }

  fun withFillColor(fillColor: Int) = apply {
    this.fillColor = fillColor
    setDrawableAndBitmap()
  }

  fun withStrokeColor(strokeColor: Int) = apply {
    this.strokeColor = strokeColor
    setDrawableAndBitmap()
  }

  fun withStrokeWidth(strokeWidth: Int) = apply {
    this.strokeWidth = strokeWidth
    setDrawableAndBitmap()
  }

  fun withDurationBetweenTwoRipples(durationBetweenTwoRipples: Long) = apply {
    this.durationBetweenTwoRipples = durationBetweenTwoRipples
  }

  fun withRippleDuration(rippleDuration: Long) = apply {
    this.rippleDuration = rippleDuration
  }

  fun withFadingOutRipple(enabled: Boolean) = apply {
    isRippleFadeOutEnabled = enabled
  }

  fun withRepeatMode(repeatMode: Int) = apply {
    check(repeatMode != ValueAnimator.RESTART || repeatMode != ValueAnimator.REVERSE) {
      "repeat mode must be one of ValueAnimator.RESTART (${ValueAnimator.RESTART}) or ValueAnimator.REVERSE (${ValueAnimator.REVERSE}), was $repeatMode"
    }
    this.repeatMode = repeatMode
  }

  private val runnable1 = Runnable {
    gOverlays[0] = googleMap.addGroundOverlay(
      GroundOverlayOptions().position(latLng, distance.toFloat()).transparency(transparency).image(BitmapDescriptorFactory.fromBitmap(backgroundImage))
    )
    overlay(0)
  }
  private val runnable2 = Runnable {
    gOverlays[1] = googleMap.addGroundOverlay(
      GroundOverlayOptions().position(latLng, distance.toFloat()).transparency(transparency).image(BitmapDescriptorFactory.fromBitmap(backgroundImage))
    )
    overlay(1)
  }
  private val runnable3 = Runnable {
    gOverlays[2] = googleMap.addGroundOverlay(
      GroundOverlayOptions().position(latLng, distance.toFloat()).transparency(transparency).image(BitmapDescriptorFactory.fromBitmap(backgroundImage))
    )
    overlay(2)
  }
  private val runnable4 = Runnable {
    gOverlays[3] = googleMap.addGroundOverlay(
      GroundOverlayOptions().position(latLng, distance.toFloat()).transparency(transparency).image(BitmapDescriptorFactory.fromBitmap(backgroundImage))
    )
    overlay(3)
  }

  private fun overlay(index: Int) {
    vAnimators[index] = ValueAnimator.ofInt(0, distance.toInt())
    vAnimators[index]?.repeatCount = ValueAnimator.INFINITE
    vAnimators[index]?.repeatMode = repeatMode
    vAnimators[index]?.duration = rippleDuration
    vAnimators[index]?.setEvaluator(IntEvaluator())
    vAnimators[index]?.interpolator = DecelerateInterpolator()
    vAnimators[index]?.addUpdateListener { valueAnimator ->
      val animatedValue = valueAnimator.animatedValue as Int
      gOverlays[index]?.setDimensions(animatedValue.toFloat())
      if (isRippleFadeOutEnabled) {
        gOverlays[index]?.transparency = 1 - (abs(distance - animatedValue).toFloat() / distance).toFloat()
      }
      if (distance - animatedValue <= 10) {
        if (latLng !== previousLatLng) {
          gOverlays[index]?.position = latLng
        }
      }
    }
    vAnimators[index]?.start()
  }

  private fun setDrawableAndBitmap() {
    drawable.setColor(fillColor)
    val d: Float = Resources.getSystem().displayMetrics.density
    val width = (strokeWidth * d).toInt() // margin in pixels
    drawable.setStroke(width, strokeColor)
    backgroundImage = drawableToBitmap(drawable)
  }

  private fun drawableToBitmap(drawable: Drawable): Bitmap? {
    if (drawable is BitmapDrawable) {
      val bitmapDrawable = drawable
      if (bitmapDrawable.bitmap != null) {
        return bitmapDrawable.bitmap
      }
    }
    val bitmap: Bitmap = if (drawable.intrinsicWidth <= 0 || drawable.intrinsicHeight <= 0) {
      Bitmap.createBitmap(1, 1, Bitmap.Config.ARGB_8888) // Single color bitmap will be created of 1x1 pixel
    } else {
      Bitmap.createBitmap(drawable.intrinsicWidth, drawable.intrinsicHeight, Bitmap.Config.ARGB_8888)
    }
    val canvas = Canvas(bitmap)
    drawable.setBounds(0, 0, canvas.width, canvas.height)
    drawable.draw(canvas)
    return bitmap
  }

  fun stopRippleMapAnimation() {
    try {
      for (i in 0 until numberOfRipples) {
        if (i == 0) {
          handlers[i]?.removeCallbacks(runnable1)
          vAnimators[i]?.cancel()
          gOverlays[i]?.remove()
        }
        if (i == 1) {
          handlers[i]?.removeCallbacks(runnable2)
          vAnimators[i]?.cancel()
          gOverlays[i]?.remove()
        }
        if (i == 2) {
          handlers[i]?.removeCallbacks(runnable3)
          vAnimators[i]?.cancel()
          gOverlays[i]?.remove()
        }
        if (i == 3) {
          handlers[i]?.removeCallbacks(runnable4)
          vAnimators[i]?.cancel()
          gOverlays[i]?.remove()
        }
      }
    } catch (e: Exception) {
    }
    isAnimationRunning = false
  }

  fun startRippleMapAnimation() {
    drawable.setColor(fillColor)
    val d: Float = Resources.getSystem().displayMetrics.density
    val width = (strokeWidth * d).toInt() // margin in pixels
    drawable.setStroke(width, strokeColor)
    backgroundImage = drawableToBitmap(drawable)
    for (i in 0 until numberOfRipples) {
      if (i == 0) {
        handlers[i] = Handler(Looper.getMainLooper())
        handlers[i]?.postDelayed(runnable1, durationBetweenTwoRipples * i)
      }
      if (i == 1) {
        handlers[i] = Handler(Looper.getMainLooper())
        handlers[i]?.postDelayed(runnable2, durationBetweenTwoRipples * i)
      }
      if (i == 2) {
        handlers[i] = Handler(Looper.getMainLooper())
        handlers[i]?.postDelayed(runnable3, durationBetweenTwoRipples * i)
      }
      if (i == 3) {
        handlers[i] = Handler(Looper.getMainLooper())
        handlers[i]?.postDelayed(runnable4, durationBetweenTwoRipples * i)
      }
    }
    isAnimationRunning = true
  }
}