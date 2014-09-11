[![Build Status](https://travis-ci.org/Zomis/Minesweeper-Analyze.svg?branch=master)](https://travis-ci.org/Zomis/Minesweeper-Analyze?branch=master)


Minesweeper Probabilities
=========================

**How does it work?**

![Small Minesweeper Board](http://i.stack.imgur.com/691Ke.png)

**Board representation**. This board can be represented like:

>     abcd
>     e13f
>     ghij
>     klmn

---

**Rules**. So we have a `1` and a `3` and we know there are 6 mines in total on the board. This can be represented as:

    a+b+c+e+g+h+i = 1
    b+c+h+i+d+f+j = 3
    a+b+c+d+e+f+g+h+i+j+k+l+m+n = 6

This is what I call **rules** (the `FieldRule` class in my code)

---

**Field Groups**. By grouping fields into which rules they are in, it can be refactored into:

    (a+e+g) + (b+c+h+i) = 1
    (b+c+h+i) + (d+f+j) = 3
    (b+c+h+i) + (d+f+j) + (k+l+m+n) = 6

These groups I call **Field Groups** (The `FieldGroup` class in the code)

---

The `RootAnalyzeImpl` class stores a collection of *rules*, and when it is getting solved it begins by splitting the fields into *groups*, then creates a `GameAnalyze` object to do the rest of the work.

**GameAnalyze**. It starts by trying to simplify things (we'll come to that later), when it can't do so any more it picks a group and assign values to it. Here I pick the `(a+e+g)` group. I find that it's best to start with a small group.

---

`(a+e+g) = 0` is chosen and a new instance of `GameAnalyze` is created, which adds `(a+e+g) = 0` to its `knownValues`.

**Simplify** (`FieldRule.simplify` method). Now we remove groups with a known value and try to deduce new known values for groups.

    (a+e+g) + (b+c+h+i) = 1

`(a+e+g)` is known, so `(b+c+h+i) = 1` remains which makes the rule solved. `(b+c+h+i) = 1` is added to `knownValues`. Next rule:

    (b+c+h+i) + (d+f+j) = 3

`(b+c+h+i) = 1` is known so we have left `(d+f+j) = 2`, making also this rule solved and another `FieldGroup` known. Last rule:

    (b+c+h+i) + (d+f+j) + (k+l+m+n) = 6

The only unknown remaining here is `(k+l+m+n)` which after removing the other groups has to have the value 3, bBecause `(b+c+h+i) + (d+f+j)` = 1 + 2.

---

**Solution** So what we know is:

    (a+e+g) = 0
    (b+c+h+i) = 1
    (d+f+j) = 2
    (k+l+m+n) = 3

As all rules have been solved and all groups have a value, this is known as a **solution** (`Solution` class).

---

Doing the same for `(a+e+g) = 1` leads, after simplification to another solution:

    (a+e+g) = 1
    (b+c+h+i) = 0
    (d+f+j) = 3
    (k+l+m+n) = 6 - 3 - 1 = 2

---

**Solution combinations**. Now we have two solutions where all the groups have values. When a solution is created, it calculates the **combinations** possible for that rule. This is done by using `nCr` ([Binomial coefficient](http://en.wikipedia.org/wiki/Binomial_coefficient)).

For the first solution we have:

    (a+e+g) = 0   --> 3 nCr 0 = 1 combination
    (b+c+h+i) = 1 --> 4 nCr 1 = 4 combinations
    (d+f+j) = 2   --> 3 nCr 2 = 3 combinations
    (k+l+m+n) = 3 --> 4 nCr 3 = 4 combinations

Multiplying these combinations we get 1*4*3*4 = 48 combinations for this solution.

As for the other solution:

    (a+e+g) = 1   --> 3 nCr 1 = 3
    (b+c+h+i) = 0 --> 4 nCr 0 = 1
    (d+f+j) = 3   --> 3 nCr 3 = 1
    (k+l+m+n) = 2 --> 4 nCr 2 = 6

3 \* 1 \* 1 * 6 = 18 combinations.

So a total of 48 + 18 = 66 combinations.

---

**Probabilities** The total combinations where a field in the `(k+l+m+n)` group is a mine is:

In first solution: 3 mines, 4 fields, 48 combinations for the solution.<br>
In second solution: 2 mines, 4 fields, 18 combinations in solution.

\$3/4 * 48 + 2/4 * 18 = 45\$

To calculate the probability we take this value divided by the total combinations of the entire board and we get: \$45 / 66 = 0.681818181818\$

**Common problems in other algorithms:**

- Other algorihtms tend to treat the "global rule" in a special way, instead of treating it just like another rule
- Other algorithms tend to treat fields individually instead of bunching them up into `FieldGroup`s

This leads to most algorithms being unable to solve [The Super board of death](http://i.stack.imgur.com/B57yE.png) in [reasonable time](http://chat.stackexchange.com/transcript/8595?m=16079276#16079276) while my algorithms solves it in about four seconds. (I'm not kidding!)

