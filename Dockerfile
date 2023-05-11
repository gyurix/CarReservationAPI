FROM maven:3-openjdk-17 as build

WORKDIR /app

COPY . .

RUN mvn -T 1C package -Dmaven.test.skip -DskipTests -Dmaven.javadoc.skip=true

FROM openjdk:17-alpine as runtime

WORKDIR /app

COPY --from=build /app/target/CarReservationAPI.jar .

CMD ["java", "-jar", "CarReservationAPI.jar"]
