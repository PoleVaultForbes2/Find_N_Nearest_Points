<meta charset="utf-8" lang="en">  <!-- this line allows UTF-8 characters in the .html file -->

**P05_KDTrees**
==============


Author
=============


| Category          | Details                                            |
|-------------------|----------------------------------------------------|
| Name              | Josh Forbes                                        |
| Computer + OS     | Windows                                            |
| Time to complete  | 9 hours                                            |
| Partner           | None                                               |
| Additional help   | YouTube video (see reflection for more info)      |

                          





Implementations
===================

kd-Tree Node
--------------

<!--
Describe the `Node` data type you used to implement the 2d tree data structure.
-->

The Node data type has a few more values than the normal Node that we have worked on so far. It has two main values that it
holds being of the Point class and Value class. Then it also has a direction which is important for finding out which value
to compare it to (x or y). The Node also has 2 pointers that go to the right or left depending on the values. This the focus
of the tree as each parent will have 2 child nodes of left or right (smaller or greater).

Nearest Neighbor
------------------

<!--
Describe your method for nearest neighbor search in a kd tree.
-->

The nearest function was used to find which point was the closest to the point we were interested in (mainly our mouse).
The best way to do this was to find the most likely place for the closest point to be. This means at each node, we either
went down the right or left side depending on which value compared better. For example, if the mouse was at point [4,5] then
we are going to set up a wall that says to check to the right side of 4. By doing this, we are half-ing the amount of searching
we have to do while going to the half that is more likely to have the closest point. We call this function recursively so that after
we find the closest point on one side of the tree we searched, we can estimate if we should go down the other side too. If
there is a chance there is a closer point then we should check, and if there isn't then there is no point in checking. This
saves us considerable amount of time.



Analysis
===============


Memory
-------

<!--
Using the 64-bit memory cost model from the textbook and lecture, give the total memory usage in bytes of your 2d tree data structure as a function of the number of points $N$.
Use tilde notation to simplify your answer (i.e., keep the leading coefficient and discard lower-order terms).
Include the memory for all referenced objects (including `Node` and `Point` objects) except for `Value` objects (because the type is unknown and the object is owned by the client).
Also, include the memory for all referenced objects, including any references to the `Direction` enum.
-->

instance of                | bytes
---------------------------|---------
`Point`                    | $\sim 32N$
`PSKDTree` with $N$ points | $\sim 88N$
`PSBruteForce`             | $\sim 96N$


Runtime
--------

<!--
How many nearest neighbor calculations can your brute-force implementation perform per second for `input100K.txt` (100,000 points) and `input1M.txt` (1 million points), where the query points are random points in the unit square?
Explain how you determined the operations per second.
(Do not count the time to read in the points from file or to build the data structure.)

Repeat the questions above with the 2d tree implementation.
--->

file            | brute force | 2d tree
----------------|-------------|---------
`input100K.txt` | $152.832$   | $0.100$
`input1M.txt`   | $1447.478$  | $0.644$

I used the stopwatch class in order to track the time it took to find the nearest points from the input files of both 100k and 1M.
As shown, the 2d tree was very efficient and was able to solve the problem much faster than the brute force method.


Known bugs / limitations
-------------------------

<!--
What bugs or limitations were troublesome?
What bugs or limitations still remain?
Are these bugs or limitations a problem in your implementation, algorithm, or something else?
How do you know that you did not miss a bug?
What additional tests did you run to ensure that your implementations are correct?
-->

<!-- ... -->

The Brute Force method wasn't as challenging and I don't think there are any bugs in this code. I did a few tests without the
visualizer, but also ran many tests on the visualizer to make sure of this. The KD tree was much harder. I was able to figure
out the helper functions like put and get fine, but the nearest was harder. The main thing I had an issue with was deciding if the
search should go down the other side of the tree or not. I also had a few trouble making the max PQ work at first because I started
with a min PQ which made more sense in my head. After looking at the project, it said to use a max PQ which I understood why
but it was harder to implement. I realized I was flipping the values of the max PQ, so I was actually keeping the max and not the min.


Reflection
===========

<!--
Describe whatever help (if any) that you received.
Don't include readings, lectures, but do include any help from people (including course staff, lab TAs, classmates, and friends) and attribute them by name.
How did you discuss the problem with others? (ex: white-board algorithms, looking over code for bugs, looking at print out results, etc.)
-->

<!-- ... -->

The main help I got was from a YouTube channel called 'Stable Sort'. I watched the 'KD-Tree Nearest Neighbor Data Structure' video
which explained how KD tree's worked which helped me visualize what I needed to do. I once again used white-boarding as my main
way to visualize and walk through my code. I used a lot of print statements around the values of max to make sure the right values
were being updated. Using this I realized that I was finding the closest point, but I wasn't updating the closest distance, so
I needed to update that in order for the visualizer to pick up on that. 

