package estaticos

import java.util.ArrayList
import java.util.LinkedHashMap
import java.util.Map
import java.util.Map.Entry
import java.util.TreeMap
import variables.mapa.Mapa
import variables.mapa.Celda
import variables.pelea.Trampa
import variables.pelea.Pelea

internal class AstarPathfinding(map: Mapa?, fight: Pelea?, cellStart: Short, cellEnd: Short) {
    private val openList: Map<Short, Node>
    private val closeList: Map<Short, Node>
    var map: Mapa? = null
    private var fight: Pelea? = null
    var cellStart: Short = 0
    var cellEnd: Short = 0
    fun getShortestPath(value: Short): ArrayList<Celda> {
        val nodeStart = Node(cellStart, null)
        openList.put(cellStart, nodeStart)
        while (!openList.isEmpty() && !closeList.containsKey(cellEnd)) {
            val dirs = charArrayOf('b', 'd', 'f', 'h')
            val nodeCurrent: Node? = bestNode()
            if (nodeCurrent.getCellId() === cellEnd && !Camino.cellArroundCaseIDisOccuped(getFight(), nodeCurrent.getCellId())) {
                return path
            }
            addListClose(nodeCurrent)
            for (loc0 in 0..3) {
                val cell: Short = Camino.getSigIDCeldaMismaDir(nodeCurrent.getCellId(), dirs[loc0], map, false)
                val node = Node(cell, nodeCurrent)
                var trampa = false
                if (fight.getTipoPelea() === 4 && fight.getLuchadorTurno() != null) {
                    for (tr in fight.getTrampas()) {
                        if (tr == null) continue
                        if (tr.getCelda().getID() === cell && tr.getLanzador().getEquipoBin() === fight.getLuchadorTurno().getEquipoBin()) {
                            trampa = true
                            break
                        }
                    }
                }
                if (map == null) break
                if (map.getCelda(cell) != null && !trampa) {
                    if (map.getCelda(cell).esCaminable(true) || cell == cellEnd) {
                        if (!Camino.haveFighterOnThisCell(cell, getFight()) || cell == cellEnd) {
                            if (!closeList.containsKey(cell)) {
                                if (openList.containsKey(cell)) {
                                    if (openList[cell].getCountG() > getCostG(node)) {
                                        nodeCurrent.setChild(openList[cell])
                                        openList[cell].setParent(nodeCurrent)
                                        openList[cell].setCountG(getCostG(node))
                                        openList[cell].setHeristic(Camino.distanciaDosCeldas(map, cell, cellEnd) * 10)
                                        openList[cell].setCountF(openList[cell].getCountG() + openList[cell].getHeristic())
                                    }
                                } else {
                                    if (value.toInt() == 0 && Camino.siCeldasEstanEnMismaLinea1(map, cell, cellEnd, dirs[loc0])) {
                                        node.setCountF(node.getCountG() + node.getHeristic() - 10)
                                    }
                                    openList.put(cell, node)
                                    nodeCurrent.setChild(node)
                                    node.setParent(nodeCurrent)
                                    node.setCountG(getCostG(node))
                                    node.setHeristic(Camino.distanciaDosCeldas(map, cell, cellEnd) * 10)
                                    node.setCountF(node.getCountG() + node.getHeristic())
                                }
                            }
                        }
                    }
                }
            }
        }
        return path
    }

    private val path: ArrayList<Celda>?
        private get() {
            var current: Node = getLastNode(closeList) ?: return null
            val path: ArrayList<Celda> = ArrayList<Celda>()
            val path2: Map<Short, Celda?> = TreeMap<Short, Celda>()
            var index = closeList.size() as Short
            while (current.getCellId() !== cellStart) {
                if (current.getCellId() !== cellStart) {
                    path2.put(index, map.getCelda(current.getCellId()))
                    current = current.getParent()
                }
                --index
            }
            index = -1
            while (path.size() !== path2.size()) {
                ++index
                if (path2[index] == null) {
                    continue
                }
                path.add(path2[index])
            }
            return path
        }

    private fun getLastNode(list: Map<Short, Node>): Node? {
        var node: Node? = null
        for (entry in list.entrySet()) {
            node = entry.getValue()
        }
        return node
    }

    private fun bestNode(): Node? {
        var bestCountF = 150000
        var bestNode: Node? = null
        for (node in openList.values()) {
            if (node.getCountF() < bestCountF) {
                bestCountF = node.getCountF()
                bestNode = node
            }
        }
        return bestNode
    }

    private fun addListClose(node: Node?) {
        if (openList.containsKey(node.getCellId())) {
            openList.remove(node.getCellId())
        }
        if (!closeList.containsKey(node.getCellId())) {
            closeList.put(node.getCellId(), node)
        }
    }

    private fun getCostG(node: Node): Int {
        var node: Node = node
        var costG: Int
        costG = 0
        while (node.getCellId() === cellStart) {
            node = node.getParent()
            costG += 10
        }
        return costG
    }

    fun getFight(): Pelea? {
        return fight
    }

    fun setFight(_fight: Pelea?) {
        fight = _fight
    }

    init {
        openList = TreeMap<Short, Node>()
        closeList = LinkedHashMap<Short, Node>()
        map = map
        setFight(fight)
        cellStart = cellStart
        cellEnd = cellEnd
    }
}