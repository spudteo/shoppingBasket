# Shopping Basket
Teo Spadotto's Assignment

## Prerequisites
To run this project, you need:
- **Java JDK** (version 11 or higher)
- **SBT** (any recent version)


## Development Environment
Project dependencies:
- **Scala**: 2.12.18
- **SBT**: 1.10.7
- **ScalaTest**: 3.2.17 (for testing)
- **ScalaFix**: 0.11.1 (for linting)
- **Java**: OpenJDK 11.0.25

## Tested Environment
Project dependencies:
- **Scala**: 2.12.18
- **SBT**: 1.10.7
- **ScalaTest**: 3.2.17 (for testing)
- **ScalaFix**: 0.11.1 (for linting)
- **Java**: OpenJDK 11.0.25, OpenJDK 21.0.6
- **Operating Systems**: macOS, Windows 11

## Quick Start

### Clone the Repository
```bash
git clone https://github.com/spudteo/shoppingBasket.git
cd shoppingBasket
```

### Run Tests
```bash
sbt test
```

### Run Application
```bash
sbt run
```

## Running Options

The application can be run in two modes:

### 1. Detailed Output
Shows all calculations:
```bash
sbt "run -d bread bread soup soup soup soup"
```

Example output:
```
Date: 2025/02/08

=== Detailed Receipt ===
===========================================================
Bread                               x  2 @ £  0.80 = £  1.60
Soup                                x  4 @ £  0.50 = £  2.00
===========================================================

Subtotal:                           £  3.60
Buy 2 Soup Get 1 Bread 50% off:     £  0.80
Total price:                        £  2.80
```

### 2. Simple Output
Shows only final price:
```bash
sbt "run bread bread soup soup soup soup"
```

Example output:
```
Date: 2025/02/08

Subtotal:                           £  3.60
Buy 2 Soup Get 1 Bread 50% off:     £  0.80
Total price:                        £  2.80
```

## Project Structure
```
shopping-basket/
├── src/
│   ├── main/
│   │   ├── scala/             # Source code
│   │   └── resources/         # Configuration files
│   └── test/
│       └── scala/             # Tests
├── build.sbt                  # Build configuration
├── project/                   # Additional SBT configurations
├── .scalafmt.conf             # Scalafmt configuration
├── .scalafix.conf             # Scalafix configuration
└── README.md                  # Documentation
```

## Offers and Items Management System

### Current Offer Structure

The system supports two types of offers configured in `offers.properties`:

#### 1. Percentage Discount (PERCENTAGE_DISCOUNT)
```properties
# 10% off Apples
offer1.type=PERCENTAGE_DISCOUNT
offer1.name=10% off Apples
offer1.item=APPLES
offer1.percentage=0.10
offer1.startDate=2025-01-01
offer1.endDate=2025-12-31
offer1.enabled=true
offer1.stackable=true
```

#### 2. Quantity-Based Discount (DISCOUNT_WITH_QUANTITY)
```properties
# Buy 2 Soup Get Bread Half Price
offer2.type=DISCOUNT_WITH_QUANTITY
offer2.name=Buy 2 Soup Get Bread Half Price
offer2.itemToBuy=soup
offer2.itemToDiscount=BREAD
offer2.quantityToBuyForDiscount=2
offer2.quantityDiscounted=1
offer2.discountPercentage=0.50
offer2.startDate=2025-01-01
offer2.endDate=2025-12-31
offer2.enabled=true
offer2.stackable=true
```

### Adding New Offers

#### Percentage Discount
```properties
offerX.type=PERCENTAGE_DISCOUNT
offerX.name=Offer Name
offerX.item=ITEM_NAME
offerX.percentage=0.XX
offerX.startDate=YYYY-MM-DD
offerX.endDate=YYYY-MM-DD
offerX.enabled=true
offerX.stackable=true
```

#### Quantity Discount
```properties
offerX.type=DISCOUNT_WITH_QUANTITY
offerX.name=Offer Name
offerX.itemToBuy=item_to_buy
offerX.itemToDiscount=ITEM_TO_DISCOUNT
offerX.quantityToBuyForDiscount=N
offerX.quantityDiscounted=M
offerX.discountPercentage=0.XX
offerX.startDate=YYYY-MM-DD
offerX.endDate=YYYY-MM-DD
offerX.enabled=true
offerX.stackable=true
```

### Adding New Items

Items are defined in `items.properties`. To add a new item:

```properties
item_name.name=Display Name
item_name.price=X.XX
```

#### Practical Example
```properties
pizza.name=Pizza Margherita
pizza.price=8.50
```

### Important Notes:
- Item names are case-insensitive
- Dates must be in YYYY-MM-DD format
- Prices must use decimal point (not comma)
- Discounts are in decimal format (0.10 = 10%)
- `enabled=true/false` controls offer activation
- `stackable=true/false` allows combination with other offers

## Development Tools

### Code Formatting
```bash
sbt scalafmt
```

### Code Linting
```bash
sbt scalafix
```


