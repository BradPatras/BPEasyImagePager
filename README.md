# BPEasyImagePager
<img src="http://i.imgur.com/BztM14H.png" alt="example" width="180" height="290">

## Add via Gradle
***
In your project build.gradle file, add a reference to the repository
```groovy
allprojects {
	repositories {
		//...
		maven { url 'https://dl.bintray.com/bradpatras/android' }
	}
}
```


Then in your module build.gradle file, add the dependency 
```groovy
dependencies {
	//...
	compile 'com.iboism.bpeasyimagepager:bpeasyimagepager:0.1.0'
}
```

## Usage
***
The easiest way to get started is to add the BPEasyImagePager to your xml layout
```xml
 <com.iboism.bpeasyimagepager.BPEasyImagePager
        android:id="@+id/placeholder"
        android:layout_width="wrap_content"
        android:layout_height="300dp"
        app:layout_constraintLeft_toLeftOf="parent"
        app:layout_constraintRight_toRightOf="parent"
        app:layout_constraintTop_toBottomOf="parent"/>
```
Then, when you assign your views in code, just give the BPEasyImagePager an array of urls and it will handle the rest
```java
ArrayList<String> arr = new ArrayList<>();
arr.add("http://i.imgur.com/v60CSTt.jpg");
arr.add("http://i.imgur.com/5OQ7Pug.jpg");
arr.add("http://i.imgur.com/8cBYgMI.jpg");

BPEasyImagePager pager = (BPEasyImagePager) findViewById(R.id.placeholder);
pager.setImages(arr);
```

## Customization
***
Just about everything that has a default value can be customized by you, from the animation times to the drawables for the page indicators. 

You can also add a listener to receive callbacks when a new page is selected.
