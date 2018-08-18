# CircleView

![](https://user-images.githubusercontent.com/2732303/43355065-b89a9ec2-9288-11e8-853f-b274a35ea661.png)


Add it in your root build.gradle at the end of repositories:

```
allprojects {
    repositories {
        ...
        maven { url 'https://jitpack.io' }
    }
}
```

Add the dependency

```
dependencies {
    implementation 'com.github.herokotlin:CircleView:0.0.1'
}
```

```xml
<com.github.herokotlin.circleview.CircleView
    android:id="@+id/circleView"
    android:layout_width="wrap_content"
    android:layout_height="wrap_content"

    app:circle_view_center_color="#EEE"
    app:circle_view_center_radius="80dp"
    app:circle_view_center_image="@drawable/avatar"

    app:circle_view_ring_color="#CCC"
    app:circle_view_ring_width="20dp"

    app:circle_view_track_color="#FF0000"
    app:circle_view_track_width="10dp"

    app:circle_view_track_value="0.5"
    app:circle_view_track_offset="5dp"
    />
```

```kotlin
import com.github.herokotlin.circleview.Callback

circleView.callback = object: Callback {
    override fun onTouchDown() {
        circleView.centerColor = Color.parseColor("#EEEEEE")
        circleView.centerImage = R.drawable.image
        circleView.invalidate()
    }

    override fun onTouchUp(inside: Boolean) {
        circleView.centerColor = Color.parseColor("#FFFFFF")
        circleView.centerImage = R.drawable.avatar
        circleView.invalidate()
    }

    override fun onTouchMove(x: Float, y: Float) {

    }


    override fun onTouchEnter() {
        circleView.centerColor = Color.parseColor("#EEEEEE")
        circleView.invalidate()
    }

    override fun onTouchLeave() {
        circleView.centerColor = Color.parseColor("#FFFFFF")
        circleView.invalidate()
    }
}
```# CircleView

   ![](https://user-images.githubusercontent.com/2732303/43355065-b89a9ec2-9288-11e8-853f-b274a35ea661.png)


   Add it in your root build.gradle at the end of repositories:

   ```
   allprojects {
       repositories {
           ...
           maven { url 'https://jitpack.io' }
       }
   }
   ```

   Add the dependency

   ```
   dependencies {
       implementation 'com.github.herokotlin:CircleView:0.0.1'
   }
   ```

   ```xml
   <com.github.herokotlin.circleview.CircleView
       android:id="@+id/circleView"
       android:layout_width="wrap_content"
       android:layout_height="wrap_content"

       app:circle_view_center_color="#EEE"
       app:circle_view_center_radius="80dp"
       app:circle_view_center_image="@drawable/avatar"

       app:circle_view_ring_color="#CCC"
       app:circle_view_ring_width="20dp"

       app:circle_view_track_color="#FF0000"
       app:circle_view_track_width="10dp"

       app:circle_view_track_value="0.5"
       app:circle_view_track_offset="5dp"
       />
   ```

   ```kotlin
   import com.github.herokotlin.circleview.Callback

   circleView.callback = object: Callback {
       override fun onTouchDown() {
           circleView.centerColor = Color.parseColor("#EEEEEE")
           circleView.centerImage = R.drawable.image
           circleView.invalidate()
       }

       override fun onTouchUp(inside: Boolean) {
           circleView.centerColor = Color.parseColor("#FFFFFF")
           circleView.centerImage = R.drawable.avatar
           circleView.invalidate()
       }

       override fun onTouchMove(x: Float, y: Float) {

       }


       override fun onTouchEnter() {
           circleView.centerColor = Color.parseColor("#EEEEEE")
           circleView.invalidate()
       }

       override fun onTouchLeave() {
           circleView.centerColor = Color.parseColor("#FFFFFF")
           circleView.invalidate()
       }
   }
   ```