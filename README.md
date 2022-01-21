# Android SDK Utils

This is a library that contains utilities which are useful when developing Android SDKs. They are intended for use internally by Rakuten's SDKs.

## This page covers

* [Setup](#setup)
* [Usage](#usage)
* [Changelog](#changelog)

## Setup

### 1. Add dependency to build.gradle

Add to your `build.gradle`:

```groovy
repositories {
  mavenCentral()
}

dependency {
  implementation "io.github.rakutentech.sdkutils:sdk-utils:$version"
}
```

### 2. Set initialization order (optional)

You only need to set this if you are facing an Exception when trying to use features of this SDK at launch time.

This SDK uses a `ContentProvider` in order to automatically initialize. If you are also using a ContentProvider in your App
or you are developing an SDK which uses a ContentProvider to automatically initialize, then you may need to manually set the
`initOrder` for this SDK so that it is initialized first. This can be set in `AndroidManifest.xml`:

```xml
  <application>
    <!-- The `initOrder` for this SDK is set to `99` by default. -->
    <!-- Set it to something higher to make it initialize before your own ContentProvider. -->
    <provider
      tools:replace="android:initOrder"
      android:name="com.rakuten.tech.mobile.sdkutils.SdkUtilsInitProvider"
      android:authorities="${applicationId}.SdkUtilsInitProvider"
      android:exported="false"
      android:initOrder="100" />
  </application>
```

## Usage

### OkHttp Header Interceptor

This library contains an extension function for `OkHttpClient.Builder` so you can more easily add headers to all network requests which are made using that client.

```kotlin
private val client = OkHttpClient.Builder()
    .addHeaderInterceptor(
        "header_name" to "header_value",
        "another_header" to "another_value"
    ).build()
```

### Application Info

The `AppInfo` class can be used to retrieve some properties of the App which normally require a `Context` to retrieve. This class is automatically initialized, so no `Context` needs to be provided.

```kotlin
val appName = AppInfo.instance.name
val appVersion = AppInfo.instance.version
```

### Logging Utility

This library contains a simple logging facility.

Logging conventions are:

* Debug: for SDK developers. Will print source file, method and line.
* Info: for SDK consumers. They should know, but is no problem.
* Warn: for SDK consumers. An unexpected situation, that the SDK can recover from.
* Error: for SDK consumers: An error that may cause the SDK to stop working.

By default only info, warn and error are logged. Debug is only logged if [Logger.setDebug] is called with `true`.

The level of the stack trace to be logged is set by default to 5 (it's the trace level of the running thread when SDKUtils is implemented in an application). 
You may need to upgrade the level using ```Logger.setDebugLevel(yourLevel)``` if SDKUtils is implemented in another SDK.

```kotlin
Logger.setDebugLevel(6)
private val log = Logger(TAG)
```

All log calls come in 2 variants:

* ```log(String template, Object.. args)``` - will use ```String.format``` to format the string.
* ```log(Throwable cause, String template, Object.. args)``` - same as above, but will add a "Caused by" and stacktrace to the log.

```kotlin
private val log = Logger(MainActivity::class.java.simpleName)

//enable debug logs (by default only info, warn and error are logged)
Logger.setDebug(true)

//Examples:
log.debug("simple debug log") // simple debug log  
log.debug("simple debug log at %s", listOf(Date())) // simple debug log at [Mon Dec 20 16:51:12 GMT+09:00 2021]

```

### Preferences Utility

Please check the following sample code to use the preferences utility feature for caching data.

```kotlin
// We can put int, string, float, long, string set and boolean to cache in preferences
PreferencesUtil.putString(appContext, preferencesFileName, key, value)

// We can get int, string, float, long, string set and boolean to retrieve from preferences
PreferencesUtil.getString(appContext, preferencesFileName, key, defaultValue)
```

### Json Utility

This utility can be used to convert a JSON string loaded from app resources to an equivalent object.

#### Examples

```kotlin
//deserializes a Json loaded from app resources into a generic type object.
val path = "my-file.json"
val devices: List<Device>? = gson.fromResources(path, object: TypeToken<List<Device>>(){}.type)

//deserializes a Json loaded from app resources into an object.
val path = "my-file.json"
val device: Device? = gson.fromResources(path, Device::class.java)
```

### Common Utility

Please check the following sample code to use the extension function (s).

```kotlin
// To check the device mode setting, i.e. dark/ light
Context.isDarkMode(context)

// To check the play service of device
Activity.checkPlayServices(activity)

// To get the Sha256 digest data
String.getSha256HashData("Test")

// To get the MD5 digest data
String.getMD5HashData("Test")

```

### Networking Utility

The following extension functions make networking framework easy to use, check the sample code to know more.

<ul>
<li>

```HttpCallback``` : a Callback Class that communicate the remote server response.

 ```kotlin
// by using an HttpCallback object:
client.newCall(request).enqueue(HttpCallback(
    { response ->  ... },{ exception ->  ... }
))

 //or by using a lambda function:

 //1- create your function e.g.
 fun get(success: (response: Response) -> Unit, failure: (exception: Exception) -> Unit) {
     val request = Request.Builder().url(url).build()
     ...
     client.newCall(request).enqueue(HttpCallback(success, failure))
 }

 // 2- get the response
 get({ response = it  ... }, { exception = it  ... })
```

</li>
<li>

Kotlin extension to get a reference of ```Retrofit```

```kotlin
//Get a reference of Retrofit using the default parameters 
val retrofit = Retrofit.Builder().build("your_baseUrl")

// Get a reference of Retrofit using your personalysed parameter(s) 
val okHttpClient: OkHttpClient = your_httpClient
val gsonConverterFactory: GsonConverterFactory = your_converterFactory
val executor: ExecutorService = your_executor

val retrofit = Retrofit.Builder().build("your_baseUrl", okHttpClient gsonConverterFactory, executor)

```

</li>
</ul>

## Changelog

### v0.3.0 (In progress)

* SDKCF-4686: Moved SharedPreferences handling and App/Env Info retrieval to SDKUtils. Please see [usage](#preferences-utility) section for details.
* SDKCF-4685: Added APIs for:
  1. logging facility, Please see [usage](#logging-utility) section for details.
  2. Json deserializer utility, Please see [usage](#json-utility) section for details.
* SDKCF-4688: Add common class extension to SDKUtils. Please see [usage](#common-utility) section for details.
* SDKCF-4687: Added APIs for networking facility, Please see [usage](#networking-utility) section for details.

### v0.2.0 (2021-03-05)

* Changed Maven Group ID to `io.github.rakutentech.sdkutils`. You must upudate your dependency declaration to `io.github.rakutentech.sdkutils:sdk-utils:0.2.0`
* Migrated publishing to Maven Central due to Bintray/JCenter being [shutdown](https://jfrog.com/blog/into-the-sunset-bintray-jcenter-gocenter-and-chartcenter/). You must add `mavenCentral()` to your `repositories``.

### v0.1.1 (2019-11-29)

* Changed Device OS header from `ras-device-os` to `ras-os-version`.

### v0.1.0 (2019-11-22)

* Initial release.
