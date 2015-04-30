library(foreach)
library(iterators)
library(GA)

f <- function(x1, x2) 21.5 + x1 * sin(4 * pi * x1) + x2 * sin(20 * pi * x2)
r1 <- seq(-3.0, 12.1, 0.01)
r2 <- seq(4.1, 5.8, 0.01)
v <- outer(r1, r2, f)
persp3D(r1, r2, v, theta = 30, phi = 25)
filled.contour(r1, r2, v, color.palette = jet.colors)

result <- ga(type = "real-valued", fitness = function(x) f(x[1], x[2]), min = c(-3.0, 4.1), max = c(12.1, 5.8))
print(summary(result))
plot(result)