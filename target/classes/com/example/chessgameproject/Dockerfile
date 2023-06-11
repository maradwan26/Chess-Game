FROM openjdk

LABEL image.name="javafx-app"

COPY . .

RUN apt-get update && apt-get install -y --no-install-recommends xvfb openjfx && rm -rf /var/lib/apt/lists/*

WORKDIR /app

ENTRYPOINT ["java", "--module-path", "lib", "com.example.chessgameproject.Main"]


CMD ["java", "com.example.chessgameproject.Main"]