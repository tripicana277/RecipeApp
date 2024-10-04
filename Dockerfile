# ベースイメージとしてOpenJDK 21のslim版を指定
FROM openjdk:21-jdk-slim

# 作業ディレクトリを設定
WORKDIR /app

# ローカルのJARファイルをコンテナ内にコピー
COPY build/libs/app.jar app.jar

# コンテナ実行時にJARファイルを起動
ENTRYPOINT ["java", "-jar", "/app/app.jar"]
