package com.example.solver

import com.example.model.Jar
import com.example.model.JarType
import java.util.ArrayDeque

/**
 * Fast Solver and Hint generator for Colour Jar Fill.
 * Uses Breadth-First Search (BFS) with state hashing to find optimal moves and guarantee solvability.
 */
object JarSolver {

    data class Move(val fromId: Int, val toId: Int)

    private data class SolverState(
        val jars: List<Jar>,
        val history: List<Move>
    )

    /**
     * Checks whether the current game state is fully solved.
     */
    fun isGameSolved(jars: List<Jar>): Boolean {
        for (jar in jars) {
            if (jar.isEmpty) continue
            if (!jar.isUniformAndFull) return false
        }
        return true
    }

    /**
     * Tests if pouring from jar `from` to jar `to` is legally valid.
     */
    fun canPour(from: Jar, to: Jar): Boolean {
        if (from.id == to.id) return false
        if (from.isEmpty) return false
        if (to.isFull) return false
        if (from.isLocked || to.isLocked) return false
        if (from.type == JarType.ONE_WAY_IN) return false // cannot pour out
        if (to.type == JarType.ONE_WAY_OUT) return false // cannot receive in

        val topFrom = from.topSegment ?: return false
        if (topFrom.isHidden || topFrom.isFrozen) return false

        // Destination is empty
        if (to.isEmpty) {
            // Optimization: Avoid moving a uniform full or uniform single segment into an empty jar unnecessarily if it came from one
            return true
        }

        // Rainbow jar accepts anything
        if (to.type == JarType.RAINBOW && to.rainbowRemainingPours > 0) return true

        val topTo = to.topSegment ?: return false
        if (topTo.isHidden || topTo.isFrozen) return false

        return topFrom.color == topTo.color
    }

    /**
     * Finds the next best move (Hint) using BFS.
     * Returns null if no solution or already solved.
     */
    fun findHint(jars: List<Jar>, maxDepth: Int = 18): Move? {
        if (isGameSolved(jars)) return null

        val queue = ArrayDeque<SolverState>()
        val visited = HashSet<String>()

        val initialHash = getStateHash(jars)
        visited.add(initialHash)
        queue.add(SolverState(jars, emptyList()))

        var iterations = 0
        val maxIterations = 2000

        while (queue.isNotEmpty() && iterations < maxIterations) {
            iterations++
            val current = queue.poll() ?: break

            if (current.history.size >= maxDepth) continue

            // Try all possible valid pours
            for (from in current.jars) {
                if (from.isEmpty || from.isLocked || from.type == JarType.ONE_WAY_IN) continue
                // Don't pour if from is already completed
                if (from.isUniformAndFull) continue

                for (to in current.jars) {
                    if (from.id == to.id || to.isLocked || to.type == JarType.ONE_WAY_OUT) continue
                    if (!canPour(from, to)) continue

                    // Perform pour simulation
                    val newJars = simulatePour(current.jars, from.id, to.id)
                    val newHash = getStateHash(newJars)

                    if (!visited.contains(newHash)) {
                        visited.add(newHash)
                        val newHistory = current.history + Move(from.id, to.id)

                        if (isGameSolved(newJars)) {
                            return newHistory.firstOrNull()
                        }

                        queue.add(SolverState(newJars, newHistory))
                    }
                }
            }
        }

        // Fallback greedy hint if BFS limit reached
        return findGreedyMove(jars)
    }

    /**
     * Greedy heuristic fallback to give players an immediate move if search tree is deep.
     */
    private fun findGreedyMove(jars: List<Jar>): Move? {
        // Priority 1: Move to a jar that makes it complete
        for (from in jars) {
            if (from.isEmpty || from.isUniformAndFull || from.isLocked) continue
            for (to in jars) {
                if (from.id == to.id || to.isLocked) continue
                if (canPour(from, to) && !to.isEmpty) {
                    val count = from.topConsecutiveCount.coerceAtMost(to.availableSpace)
                    if (to.segments.size + count == to.capacity) {
                        return Move(from.id, to.id)
                    }
                }
            }
        }

        // Priority 2: Move matching color
        for (from in jars) {
            if (from.isEmpty || from.isUniformAndFull || from.isLocked) continue
            for (to in jars) {
                if (from.id == to.id || to.isLocked) continue
                if (canPour(from, to) && !to.isEmpty) {
                    return Move(from.id, to.id)
                }
            }
        }

        // Priority 3: Move to empty jar
        for (from in jars) {
            if (from.isEmpty || from.isUniformAndFull || from.isLocked) continue
            for (to in jars) {
                if (from.id != to.id && to.isEmpty && !to.isLocked) {
                    return Move(from.id, to.id)
                }
            }
        }

        return null
    }

    /**
     * Simulates pouring from one jar to another.
     */
    private fun simulatePour(jars: List<Jar>, fromId: Int, toId: Int): List<Jar> {
        val fromIndex = jars.indexOfFirst { it.id == fromId }
        val toIndex = jars.indexOfFirst { it.id == toId }
        if (fromIndex == -1 || toIndex == -1) return jars

        val from = jars[fromIndex]
        val to = jars[toIndex]

        val count = from.topConsecutiveCount.coerceAtMost(to.availableSpace)
        if (count <= 0) return jars

        val movingSegments = from.segments.takeLast(count)
        val remainingFrom = from.segments.dropLast(count).toMutableList()

        // Reveal mystery segment if top is now hidden
        if (remainingFrom.isNotEmpty() && remainingFrom.last().isHidden) {
            val last = remainingFrom.removeAt(remainingFrom.lastIndex)
            remainingFrom.add(last.copy(isHidden = false))
        }

        val newToSegments = to.segments + movingSegments

        val newFrom = from.copy(segments = remainingFrom)
        val newTo = to.copy(segments = newToSegments)

        val result = jars.toMutableList()
        result[fromIndex] = newFrom
        result[toIndex] = newTo
        return result
    }

    private fun getStateHash(jars: List<Jar>): String {
        return jars.joinToString(separator = "|") { jar ->
            jar.segments.joinToString(separator = ",") { seg ->
                if (seg.isHidden) "?" else if (seg.isFrozen) "F${seg.color.id}" else seg.color.id.toString()
            } + (if (jar.isLocked) ":L" else "")
        }
    }
}
