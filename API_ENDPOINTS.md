# Product API Endpoints

Base URL: `http://localhost:8080/api/products`

## Available Endpoints

### 1. Get All Products

**GET** `/api/products`

Returns all products with calculated prices.

**Example:**

```bash
curl http://localhost:8080/api/products
```

**Response:** 8 products ✓

---

### 2. Get Products by Popularity

**GET** `/api/products/by-popularity?order={asc|desc}`

Returns products sorted by popularity score.

**Parameters:**

- `order` (optional): `desc` (default) or `asc`

**Examples:**

```bash
# Highest popularity first (default)
curl http://localhost:8080/api/products/by-popularity

# Lowest popularity first
curl "http://localhost:8080/api/products/by-popularity?order=asc"
```

**Test Results:**

- DESC: Returns products sorted 0.92 → 0.51 ✓
- ASC: Returns products sorted 0.51 → 0.92 ✓

---

### 3. Search Products by Name

**GET** `/api/products/search?name={searchTerm}`

Search products by name (case-insensitive, partial matching).

**Parameters:**

- `name` (required): Search term

**Example:**

```bash
curl "http://localhost:8080/api/products/search?name=Ring%201"
```

**Test Results:** Returns 1 product (Engagement Ring 1) ✓

---

### 4. Filter by Price Range

**GET** `/api/products/filter/price?minPrice={min}&maxPrice={max}`

Filter products by price range.

**Parameters:**

- `minPrice` (optional): Minimum price
- `maxPrice` (optional): Maximum price

**Examples:**

```bash
# Products under $700
curl "http://localhost:8080/api/products/filter/price?maxPrice=700"

# Products between $500 and $900
curl "http://localhost:8080/api/products/filter/price?minPrice=500&maxPrice=900"

# Products over $800
curl "http://localhost:8080/api/products/filter/price?minPrice=800"
```

**Test Results:**

- `maxPrice=700`: Returns 4 products ✓
- `minPrice=500&maxPrice=900`: Returns 3 products ✓

---

### 5. Filter by Popularity Score

**GET** `/api/products/filter/popularity?minScore={min}&maxScore={max}`

Filter products by popularity score range.

**Parameters:**

- `minScore` (optional): Minimum popularity score (0.0 - 1.0)
- `maxScore` (optional): Maximum popularity score (0.0 - 1.0)

**Examples:**

```bash
# High popularity products (0.85+)
curl "http://localhost:8080/api/products/filter/popularity?minScore=0.85"

# Medium popularity products (0.6 - 0.8)
curl "http://localhost:8080/api/products/filter/popularity?minScore=0.6&maxScore=0.8"
```

**Test Results:**

- `minScore=0.85`: Returns 4 products ✓

---

### 6. Combined Filter

**GET** `/api/products/filter?minPrice={min}&maxPrice={max}&minPopularity={min}&maxPopularity={max}`

Filter products by both price range AND popularity score.

**Parameters:**

- `minPrice` (optional): Minimum price
- `maxPrice` (optional): Maximum price
- `minPopularity` (optional): Minimum popularity score
- `maxPopularity` (optional): Maximum popularity score

**Examples:**

```bash
# Premium products: high price and high popularity
curl "http://localhost:8080/api/products/filter?minPrice=800&minPopularity=0.85"

# Budget products: low price and any popularity
curl "http://localhost:8080/api/products/filter?maxPrice=500"

# Mid-range popular products
curl "http://localhost:8080/api/products/filter?minPrice=500&maxPrice=900&minPopularity=0.8"
```

**Test Results:**

- `minPrice=500&maxPrice=900&minPopularity=0.8`: Returns 2 products ✓

---

## Response Format

All endpoints return a JSON array of `ProductResponse` objects wrapped in `ResponseEntity`:

```json
[
  {
    "id": 1,
    "name": "Engagement Ring 1",
    "popularityScore": 0.85,
    "weight": 2.1,
    "images": {
      "yellow": "https://cdn.shopify.com/...",
      "rose": "https://cdn.shopify.com/...",
      "white": "https://cdn.shopify.com/..."
    },
    "price": 484.77
  }
]
```

**Fields:**

- `id`: Product ID
- `name`: Product name
- `popularityScore`: Popularity score (0.0 - 1.0)
- `weight`: Product weight in grams
- `images`: Object containing URLs for yellow, rose, and white gold versions
- `price`: Calculated price in USD (dynamically calculated)

---

## Price Calculation

Price is calculated dynamically using the formula:

```
Price = (popularityScore + 1) × weight × goldPricePerGram
```

**Where:**

- `popularityScore`: Product's popularity (0.0 - 1.0)
- `weight`: Product weight in grams
- `goldPricePerGram`: Current gold price fetched from external API
  - Updated hourly with 1-hour cache
  - Fallback: $124/gram if API is unavailable

---

## Test Results Summary

All endpoints tested successfully ✓

| Endpoint                                                           | Test                     | Result               |
| ------------------------------------------------------------------ | ------------------------ | -------------------- |
| `/api/products`                                                    | Get all products         | ✓ Returns 8 products |
| `/api/products/by-popularity`                                      | Sort DESC                | ✓ Sorted 0.92 → 0.51 |
| `/api/products/by-popularity?order=asc`                            | Sort ASC                 | ✓ Sorted 0.51 → 0.92 |
| `/api/products/search?name=Ring%201`                               | Search by name           | ✓ Returns 1 product  |
| `/api/products/filter/price?maxPrice=700`                          | Filter by max price      | ✓ Returns 4 products |
| `/api/products/filter/price?minPrice=500&maxPrice=900`             | Filter by price range    | ✓ Returns 3 products |
| `/api/products/filter/popularity?minScore=0.85`                    | Filter by min popularity | ✓ Returns 4 products |
| `/api/products/filter?minPrice=500&maxPrice=900&minPopularity=0.8` | Combined filter          | ✓ Returns 2 products |

---

## Architecture

### Layered Structure

```
Controller (ProductController)
    ↓ Returns ResponseEntity<ProductResponse>
Service (ProductService)
    ↓ Business logic & filtering
PriceCalculator
    ↓ Price calculation
GoldPriceService
    ↓ External API integration
Repository (ProductRepository)
    ↓ Database access
```

### Key Components

**Controller Layer:**

- `ProductController.java` - REST endpoints
- CORS enabled for all origins
- All parameters are optional (except search name)
- Returns DTOs wrapped in ResponseEntity

**Service Layer:**

- `ProductService.java` - Business logic
- `PriceCalculator.java` - Price calculations
- `GoldPriceService.java` - External API integration

**Data Layer:**

- `ProductRepository.java` - JPA repository with custom query methods
- Efficient filtering: DB-level for popularity, in-memory for price

**DTOs:**

- `ProductResponse.java` - API response format
- `ProductFilterRequest.java` - Filter parameters encapsulation

---

## Notes

- Price filtering is done in-memory after price calculation (prices are dynamic)
- Popularity filtering is done at database level (more efficient)
- Search is case-insensitive and supports partial matching
- Gold prices are cached for 1 hour to reduce external API calls
- All endpoints support CORS from any origin
