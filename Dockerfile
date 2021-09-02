# build jar file
from gradle as build

workdir /cli

add src src
add gradle gradle
add gradlew .
add build.gradle .
add settings.gradle .

run ./gradlew build


# copy jar file into final container
from openjdk

copy --from=build /cli/build/libs/* /usr/share/structurizr/

workdir cli/

entrypoint ["java", "-jar", "/usr/share/structurizr/structurizr-cli-1.12.1.jar"]
