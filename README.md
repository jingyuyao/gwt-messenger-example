# Example Gradle + GWT + Google App Engine project

## Dependencies

### Build (use latest version for all)

- Java 8
- Gradle
- gwt-gradle-plugin by steffenschaefer
- gradle-appengine-plugin by Google

### SDKs (managed by Gradle)

- Google App Engine 1.0
- GWT 2.7.0

## How to...

### Development mode

	>>>gradlew appengineRun
	>>>gradlew gwtSuperDev

These commands boots up two local servers:

- SuperDevMode at `http://localhost:9876` which listens to requests to recompile your code
- AppEngine at `http://localhost:8080` which serves the app itself

Go to SuperDevMode server in your browser and follow the instructions to save the bookmarks that allow you to recompile your app.
Now you can go to the AppEngine server to view your app.
If you don't see anything, click on the `Dev Mode On` bookmark you saved earlier and then hit `Compile` on the opened modal.
This will sent a request to SuperDevMode server to initiate a recompile and reload your page.

You can avoid the modal by bookmarking the `Compile` button for one-click compile + reload.

### Deploy

	>>>gradlew appengineUpdate

This uploads the app to App Engine. It will override the version (defined in `appengine-web.xml`) on App Engine if it exists or else it will create that version.

If the uploaded app has a new version, run:

	>>>gradlew appengineSetDefaultVersion

to tell App Engine to start serving the new version on production.

## Project structure

### Layout

- `src/main/java/webapp/` contains web application sources as specificed by the `war` plugin for Gradle.
- `src/main/java/com/jingyu/example/` contains all the GWT and App Engine application logic.

### Important files

- `src/main/java/com/jingyu/example/Example.gwt.xml` this is the main GWT configuration file.
- `src/main/webapp/WEB-INF/web.xml` this is the main url routing file.
- `src/main/webapp/WEB-INF/appengine-web.xml` configuration file for App Engine.

## Resources

### Official links

- https://gradle.org/getting-started-gradle-java/
- http://www.gwtproject.org/
- https://cloud.google.com/appengine/docs/java/
- https://github.com/GoogleCloudPlatform/gradle-appengine-plugin
- https://github.com/steffenschaefer/gwt-gradle-plugin

### Other helpful guides (may not be compatible with this one)

- http://www.blueesoteric.com/blog/2015/05/eggg-eclipse-gwt-gae-and-gradle
- https://examples.javacodegeeks.com/core-java/gradle/gradle-gwt-integration-example/

## Todo...

- [ ] Eclipse integration
- [ ] Port over more example code and comments from the example project generated by Google Plugin for Eclipse
- [ ] More example code for both GWT and App Engine