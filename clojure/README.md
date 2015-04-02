# Evolution Programs

A bunch of EP experiments for my IS665 course at Pace University.

We started with 'R' in class and when the Professor asked us to look at implementations and libraries on other platforms
'Clojure' was my natural first choice. I found [clj-genetic](https://github.com/beloglazov/clj-genetic) and its
simplicity suited my needs at this stage. I'll probably add more experiments here as the course progresses.

## Usage

Right now, each experiment is in its own namespace and has it own `-main` method. You can run them as follows.

    $ lein run -m evolution-programs.$EXPERIMENT

While the logging functions can be passed to evolution-programs.core/run, the default behaviour it to log to the console
as well as plot the chart. For the sake of convenience, the combined logger flushes console output after logging each
generation as well as pauses between plotting the statistics for the same so that we can observe the progression of the
populations.

## Examples

    $ lein run -m evolution-programs.one-param
    Generation:   0 | Mean fitness 1.007511 | Best: 2.827826 
    Generation:   1 | Mean fitness 1.344773 | Best: 2.827826 
    Generation:   2 | Mean fitness 1.161426 | Best: 2.434751 
    Generation:   3 | Mean fitness 1.322018 | Best: 2.434751 
    Generation:   4 | Mean fitness 1.621202 | Best: 2.434751 
    Generation:   5 | Mean fitness 1.646214 | Best: 2.434751 
    Generation:   6 | Mean fitness 1.860049 | Best: 2.434751 
    Generation:   7 | Mean fitness 2.108170 | Best: 2.434751 
    Generation:   8 | Mean fitness 2.352422 | Best: 2.434751 
    Generation:   9 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  10 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  11 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  12 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  13 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  14 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  15 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  16 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  17 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  18 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  19 | Mean fitness 2.434751 | Best: 2.434751 
    Generation:  20 | Mean fitness 2.434751 | Best: 2.434751 
    {:solution (1.4460229916273573), :feasible true, :fitness 2.4347512006287895, :objective Maximize, :generation 20}

[PENDING: screenshot]
