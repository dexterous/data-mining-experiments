library(foreach)
library(iterators)
library(GA)

f <- function(x) x * sin(10 * pi * x) + 1.0
curve(f, -1, 2, 1000)

result <- ga(type = "real-valued", fitness = f, min = -1, max = 2)

print(summary(result))
plot(result)