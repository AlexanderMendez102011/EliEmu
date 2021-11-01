package estaticos

internal class Node(cellId: Short, parent: Node?) {
    var countG = 0
    var countF = 0
    var heristic = 0
    var cellId: Short = 0
    var parent: Node? = null
    var child: Node? = null

    init {
        cellId = cellId
        parent = parent
    }
}