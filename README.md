# Algorithms, Part II – Coursera (Solutions)

This repository contains my solutions to assignments and exercises from the **Algorithms, Part II** course offered by Princeton University on [Coursera](https://www.coursera.org/learn/algorithms-part2).

The course builds on the foundations from *Algorithms, Part I* and focuses on advanced data structures, graph algorithms, string algorithms, and their applications.

---

## 📂 Repository Structure
The repository is organized according to the assignment questions provided in the course. Each problem has its own implementation file(s) and sometimes a short test/demo program.

- **Question 1 – Nonrecursive Depth-First Search**  
  Implementation of DFS in an undirected graph without recursion using an explicit stack.

- **Question 2 – Diameter and Center of a Tree**  
  Linear-time algorithms to compute the diameter (longest simple path) and center (vertex minimizing max distance) of a tree.

- **Question 3 – Euler Cycle**  
  Proof and implementation (Hierholzer’s algorithm) for detecting and constructing an Euler cycle in a connected graph.

*(More assignments/solutions will be added as I progress through the course.)*

---

## ⚡ Challenges & Difficulties Faced
While working on these problems, I encountered several challenges:

- **Understanding graph traversal without recursion:**  
  Translating recursive DFS into an iterative version with a stack required careful handling of visited states and traversal order.

- **Diameter and center of a tree:**  
  It was tricky at first to prove correctness of the two BFS method for diameter and to realize that the tree’s center lies in the middle of the diameter path.

- **Euler cycle construction:**  
  Hierholzer’s algorithm was new to me. The main challenge was correctly removing edges while ensuring each edge is visited exactly once and efficiently managing adjacency lists.

- **Balancing theory and implementation:**  
  Some proofs (like Euler cycle existence ⇔ even degrees) are elegant but turning them into efficient code required rethinking.

---

## 🚀 How to Run
Each problem is implemented in Python. To run:

```bash
python3 filename.py
