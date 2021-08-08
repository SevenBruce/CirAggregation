# Circle Aggregation

## Prepare

### Java

- Open-JDK 11
- Maven

### Platform

- Linux or Windows 10
- 4G RasberryPi 4B

## Compile

``` bash
    git clone https://github.com/SevenBruce/CirAggregation.git
    mvn clean package
```

## Experiments

### Comparision with other methods

#### Linux

``` bash
bash experiments.sh
```

#### Win10

``` bash
experiments.bat
```

### Results

#### Registration

![meter_reg](python/figs/meter_reg.svg) ![total_reg](python/figs/total_reg.svg)

### Aggregation

![meter_report](python/figs/meter_report.svg) ![agg_report](python/figs/agg_report.svg)

![control_report](python/figs/control_report.svg) ![total_report](python/figs/total_report.svg)

### K

![k_register](python/figs/k_register.svg) ![k_report](python/figs/aggregation_k.svg)

### Range

![range_agg](python/figs/aggregation_range.svg)
