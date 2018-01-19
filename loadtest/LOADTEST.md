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
Running 1m test @ http://localhost:8080/
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     4.19ms   21.97ms 301.11ms   97.72%
    Req/Sec     1.78k   364.38     2.21k    81.91%
  Latency Distribution
     50%    1.03ms
     75%    1.35ms
     90%    1.84ms
     99%  140.40ms
  209981 requests in 1.00m, 18.82MB read
Requests/sec:   3496.64
Transfer/sec:    320.98KB
```
PUT с перезаписью выполняется немногт быстрее, чем просто PUT

### GET без повторов

```
ushakovilya$ wrk --latency -c4 -d1m -s loadtest/GET_no_repeats.lua  http://localhost:8080/ 
Running 1m test @ http://localhost:8080/
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency     1.32ms    2.42ms  59.41ms   94.63%
    Req/Sec     2.18k     0.88k    3.95k    59.42%
  Latency Distribution
     50%  757.00us
     75%    1.17ms
     90%    2.33ms
     99%   11.29ms
  260008 requests in 1.00m, 21.82MB read
Requests/sec:   4329.00
Transfer/sec:    372.02KB
```

### GET с повторами

Сначала запускаю wrk со скриптом before_GET_with_repeats.lua на 3 секунды, чтобы провести запись
Потом производится GET запрос, считывая всего с первых 30 id

```
ushakovilya$ wrk --latency -c4 -d1m -s loadtest/GET_with_repeats.lua  http://localhost:8080/v0/status 
Running 1m test @ http://localhost:8080/v0/status
  2 threads and 4 connections
  Thread Stats   Avg      Stdev     Max   +/- Stdev
    Latency   394.22us  722.09us  24.45ms   98.68%
    Req/Sec     5.80k   748.63     9.83k    81.10%
  Latency Distribution
     50%  317.00us
     75%  402.00us
     90%  494.00us
     99%    1.80ms
  693300 requests in 1.00m, 58.18MB read
Requests/sec:  11535.29
Transfer/sec:      0.97MB
```
Как-то подозрительно быстро