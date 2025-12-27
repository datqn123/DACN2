# ✈️ TripGo - Travel Booking API

Welcome to the backend repository of **TripGo**, a comprehensive travel booking platform built for **Đồ án Chuyên ngành 2**.

![Java](https://img.shields.io/badge/Java-17-orange)
![Spring Boot](https://img.shields.io/badge/Spring_Boot-3.0-green)
![Elasticsearch](https://img.shields.io/badge/Elasticsearch-8.11-yellow)
![Redis](https://img.shields.io/badge/Redis-Latest-red)
![Kafka](https://img.shields.io/badge/Apache_Kafka-Enabled-black)
![Docker](https://img.shields.io/badge/Docker-Enabled-blue)

## 📖 About The Project

**TripGo** is a robust backend system designed to handle:

- 🏨 **Hotel & Tour Booking**: Full reservation flow.
- 🔍 **Advanced Search**: Powered by Elasticsearch.
- 💬 **Real-time Chat**: WebSocket support for customer support.
- 💳 **Payments**: Integrated with PayOS.

## 🚀 Live Demo & API Documentation

Access the Swagger UI to test the API endpoints directly:

👉 **[Swagger UI - Render Deployment](https://tripgo-api.onrender.com/swagger-ui/index.html)**

> [!WARNING] > **Important Note for Free Tier**:
> The server is hosted on Render's Free Tier. It will spin down (sleep) after **15 minutes** of inactivity.
>
> - If the first request takes a long time or fails, please **wait 1-2 minutes** for the server to wake up and try again.

## 🛠️ Tech Stack

- **Core**: Java 17, Spring Boot
- **Database**: MySQL (Main), Redis (Caching)
- **Search Engine**: Elasticsearch
- **Message Broker**: Apache Kafka
- **Containerization**: Docker
- **Deployment**: Render

## Project Repositories

- **Backend Source**: [https://github.com/datqn123/DACN2.git](https://github.com/datqn123/DACN2.git)
- **Recommender System**: [https://github.com/datqn123/recommender-trip-go-api.git](https://github.com/datqn123/recommender-trip-go-api.git)

## �💻 Getting Started (Local Dev)

1.  **Clone the repo**:

    ```bash
    git clone https://github.com/datqn123/DACN2.git
    cd dacn2
    ```

2.  **Configuration**:

    - Update `src/main/resources/application.properties` with your local database credentials or use the `docker-compose.yml` provided.

3.  **Run with Docker** (Recommended for dependencies):
    ```bash
    docker-compose up -d
    # Then run the Spring Boot app
    ```

## 📬 Contact & Support

If you have any questions about this project/capstone, feel free to reach out!

---

_Happy Coding!_ 🚀
