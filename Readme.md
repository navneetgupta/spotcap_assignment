## Assumptions  
* Installments/CashFlows are at monthly interval.
* IRR is rounded to 9 decimal places
* APR is rounded to single decimal places as per standard.

## NOTE:  Please let me know if any assumptions are wrong and I can rework on it.

```scala
sbt run
```

## Select application to run.
* Enter 1 or 2

* Using API 

```
curl -X POST \
  http://localhost:9000/calculator \
  -H 'Content-Type: application/json' \
  -d '{
        "principal": 51020400,
        "upfrontFee": {
          "value": 1020400
        },
        "upfrontCreditlineFee": {
          "value": 0
        },
        "schedule": [
          {
            "id": 1,
            "date": "2016-10-20",
            "principal": 3595000,
            "interestFee": 1530600
          },
          {
            "id": 2,
            "date": "2016-11-21",
            "principal": 3702800,
            "interestFee": 1422800
          },
          {
            "id": 3,
            "date": "2016-12-20",
            "principal": 3813900,
            "interestFee": 1311700
          },
          {
            "id": 4,
            "date": "2017-01-20",
            "principal": 3928300,
            "interestFee": 1197300
          },
          {
            "id": 5,
            "date": "2017-02-20",
            "principal": 4046200,
            "interestFee": 1079400
          },
          {
            "id": 6,
            "date": "2017-03-20",
            "principal": 4167600,
            "interestFee": 958000
          },
          {
            "id": 7,
            "date": "2017-04-20",
            "principal": 4292600,
            "interestFee": 833000
          },
          {
            "id": 8,
            "date": "2017-05-22",
            "principal": 4421400,
            "interestFee": 704200
          },
          {
            "id": 9,
            "date": "2017-06-20",
            "principal": 4554000,
            "interestFee": 571600
          },
          {
            "id": 10,
            "date": "2017-07-20",
            "principal": 4690600,
            "interestFee": 435000
          },
          {
            "id": 11,
            "date": "2017-08-21",
            "principal": 4831400,
            "interestFee": 294200
          },
          {
            "id": 12,
            "date": "2017-09-20",
            "principal": 4976600,
            "interestFee": 149300
          }
        ]
      }'
```

## Runing test

```
sbt test
```


## Further Improvement:
* Optimize initial guess.
* Modify to include installment different interval 