FROM androidsdk/android-30
COPY / /kiosk/
WORKDIR /kiosk
RUN ./gradlew assemble