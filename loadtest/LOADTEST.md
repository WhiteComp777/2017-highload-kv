# Нагрузочное тестирование
Тестирование происходит на Macbook Pro с SSD. Хранение информации в проекте реализовано на обычных файлах.

## Тестирование

### PUT без перезаписи
Производится PUT запрос, записывая поочереди на разные id. 

```
ushakovilya$ wrk --latency -c4 -d1m -s loadtest/PUT_no_rewrite.lua  http://localhost:8080/
Running 1m test @ http://localhost:8080/v0/status
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     4.79ms   22.73ms 309.06ms   97.41%
    Req/Sec     1.34k   385.42     1.81k    76.23%
  Latency Distribution
     50%    1.32ms
     75%    1.77ms
     90%    2.85ms
     99%  140.02ms
  157904 requests in 1.00m, 14.16MB read
Requests/sec:   2628.40
Transfer/sec:    241.28KB
```

### PUT с перезаписью

Производится PUT запрос, записывая поочереди всего на 4 id. 

```
ushakovilya$ wrk --latency -c4 -d1m -s loadtest/PUT_rewrite.lua  http://localhost:8080/ 
Running 1m test @ http://localhost:8080/v0/status
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.14ms    1.38ms  41.36ms   98.65%
    Req/Sec     1.89k   133.74     2.09k    87.75%
  Latency Distribution
     50%    1.01ms
     75%    1.26ms
     90%    1.52ms
     99%    2.83ms
  225261 requests in 1.00m, 20.19MB read
Requests/sec:   3753.67
Transfer/sec:    344.58KB
```

Интересно, что такие запросы выполняются значительно быстрее запросов без перезаписи, среднее значение задержки упало на 3.5ms. Не уверен, но у меня есть гипотеза, почему так происходит. Возможно, моя ОС фактически запись на диск не производит, а просто сохраняет в кеше изменения на некоторое время, чтобы потом записать все на диск. И из-за большого количества запросов в секунду фактически запись на диск происходит только на последних запросах, а все промежуточные просто быстро презаписываются в кеше.
### GET без повторов
### GET с повторами


## Вывод