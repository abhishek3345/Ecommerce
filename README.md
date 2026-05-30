# E-Commerce Backend System

A RESTful backend for a multi-vendor e-commerce platform built with Spring Boot. The system supports two roles — **Consumers** and **Sellers** — with JWT-based authentication and role-based access control.

---

## Features

- JWT authentication with role-based authorization (Consumer / Seller)
- Sellers can manage their product listings (add, update, delete)
- Consumers can manage their shopping cart (add, update, delete items)
- Public product search by name or category
- Proper HTTP status codes for all edge cases (401, 403, 404, 409)

---

## Tech Stack

| Layer | Technology |
|---|---|
| Language | Java |
| Framework | Spring Boot |
| Security | Spring Security, JWT |
| ORM | JPA / Hibernate |
| Database | MySQL |
| API Style | REST |

---

## Database Schema

```
User         → roles (Many-to-Many)
User         → Cart (One-to-One)
User         → Product (One-to-Many, as Seller)
Cart         → CartProduct (One-to-Many)
CartProduct  → Product (Many-to-One)
Product      → Category (Many-to-One)
```

---

## API Reference

### Public Endpoints

| Method | Endpoint | Description |
|---|---|---|
| POST | `/api/public/login` | Authenticate and receive JWT |
| GET | `/api/public/product/search?keyword=` | Search products by name or category |

**Login Request**
```json
{
  "username": "bob",
  "password": "pass_word"
}
```

**Login Response** — returns JWT token string

**Search Response**
```json
[
  {
    "productId": 1,
    "productName": "Apple iPad 10.2 8th Gen WiFi iOS Tablet",
    "price": 29190.0,
    "category": { "categoryName": "Electronics" }
  }
]
```

---

### Consumer Endpoints
> Require JWT with CONSUMER role in header: `JWT: <token>`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/auth/consumer/cart` | Get current cart |
| POST | `/api/auth/consumer/cart` | Add product to cart |
| PUT | `/api/auth/consumer/cart` | Update product quantity (quantity 0 = remove) |
| DELETE | `/api/auth/consumer/cart` | Remove product from cart |

**GET Cart Response**
```json
{
  "cartId": 1,
  "totalAmount": 20.0,
  "cartProducts": [
    {
      "cpId": 1,
      "product": {
        "productId": 2,
        "productName": "Crocin pain relief tablet",
        "price": 10.0,
        "category": { "categoryName": "Medicines" }
      },
      "quantity": 2
    }
  ]
}
```

---

### Seller Endpoints
> Require JWT with SELLER role in header: `JWT: <token>`

| Method | Endpoint | Description |
|---|---|---|
| GET | `/api/auth/seller/product` | Get all products owned by seller |
| GET | `/api/auth/seller/product/{productId}` | Get product by ID |
| POST | `/api/auth/seller/product` | Add new product |
| PUT | `/api/auth/seller/product` | Update existing product |
| DELETE | `/api/auth/seller/product/{productId}` | Delete product (404 if not owned) |

**Product JSON**
```json
{
  "productId": 3,
  "productName": "iPhone 12 Pro Max",
  "price": 98000.0,
  "category": {
    "categoryId": 2,
    "categoryName": "Electronics"
  }
}
```

---

## Status Code Reference

| Code | Meaning |
|---|---|
| 200 | Success |
| 201 | Resource created |
| 400 | Bad request (e.g. missing keyword) |
| 401 | Missing or invalid JWT |
| 403 | Role not authorized for this endpoint |
| 404 | Resource not found |
| 409 | Conflict (e.g. product already in cart) |

---

## Running Locally

### Prerequisites
- Java 17+
- MySQL
- Maven

### Setup

1. Clone the repository
```bash
git clone https://github.com/abhishek3345/your-repo-name.git
cd your-repo-name
```

2. Configure your database in `src/main/resources/application.properties`
```properties
spring.datasource.url=jdbc:mysql://localhost:3306/ecommerce
spring.datasource.username=your_username
spring.datasource.password=your_password
```

3. Build and run
```bash
mvn spring-boot:run
```

4. The API will be available at `http://localhost:8080`

---

## Default Seed Data

The database is pre-loaded with the following data for testing:

**Users**

| Username | Role |
|---|---|
| jack | CONSUMER |
| bob | CONSUMER |
| apple | SELLER |
| glaxo | SELLER |

**Password for all users:** `pass_word`

**Categories:** Fashion, Electronics, Books, Groceries, Medicines
