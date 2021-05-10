package gson_gen

import Dummy
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.RGB
import org.eclipse.swt.graphics.RGBA
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

// helper class
class Stack<E>() {
    val stackObj = mutableListOf<E>()
    fun read(): E {
        return stackObj[stackObj.size - 1]
    }
    fun push(value: E) {
        stackObj.add(value)
    }
    fun pull(): E {
        return stackObj.removeLast()
    }
}

class WindowTree(obj: Jvalue) {
    val shell: Shell
    var tree: Tree

    inner class JvalueTreeVisitor(): Visitor {

        // use stack for refer parent node
        var treeStack = Stack<TreeItem>()

        private fun getTreeItem(): TreeItem {
            val current: TreeItem
            if (treeStack.stackObj.size < 1) {
                current = TreeItem(tree, SWT.NONE)
            } else {
                current = TreeItem(treeStack.read(), SWT.NONE)
            }
            return current
        }

        override fun visit(value: Jnode) {
            val current = getTreeItem()
            current.text = value.key
            current.data = value.value
            treeStack.push(current)
        }

        override fun visit(value: Jobject) {
            val current = getTreeItem()
            current.text = "object"
            current.data = value
            treeStack.push(current)
        }

        override fun visit(value: Jarray) {
            val current = getTreeItem()
            current.text = "array"
            current.data = value
            treeStack.push(current)
        }

        override fun visit(value: Jstring) {
            val current = getTreeItem()
            current.text = "string"
            current.data = value
        }

        override fun visit(value: Jnumber) {
            val current = getTreeItem()
            current.text = "number"
            current.data = value
        }

        override fun visit(value: Jbool) {
            val current = getTreeItem()
            current.text = "bool"
            current.data = value
        }

        override fun visit(value: Jnull) {
            val current = getTreeItem()
            current.text = "null"
            current.data = Jstring("null")
        }

        override fun afterVisit(value: Jnode) {
            if(treeStack.stackObj.size > 0) {
                treeStack.pull()
            }
        }

        override fun afterVisit(value: Jobject) {
            if(treeStack.stackObj.size > 0) {
                treeStack.pull()
            }
        }

        override fun afterVisit(value: Jarray) {
            if(treeStack.stackObj.size > 0) {
                treeStack.pull()
            }
        }

        override fun afterVisit(value: Jstring) {}

        override fun afterVisit(value: Jnumber) {}

        override fun afterVisit(value: Jbool) {}

        override fun afterVisit(value: Jnull) {}

    }

    // stringify to JSON with tabs
    class StringifyTabVisitor : Visitor {
        var str = ""
        var depth = 0
        override fun visit(node: Jnode) {
            str += "\t".repeat(depth) + "\"${node.key}\":\n"
            depth ++
        }

        override fun visit(obj: Jobject) {
            str += "\t".repeat(depth) + "{\n"
            depth ++
        }

        override fun visit(arr: Jarray) {
            str += "\t".repeat(depth) +  "[\n"
            depth ++
        }

        override fun visit(value: Jstring) {
            str += "\t".repeat(depth) +  "\"" + value.value + "\",\n"
        }

        override fun visit(value: Jnumber) {
            str += "\t".repeat(depth) + value.value.toString()
        }

        override fun visit(value: Jbool) {
            str += "\t".repeat(depth) + value.value.toString()
        }

        override fun visit(value: Jnull) {
            str += "\t".repeat(depth) + "null\n"
        }

        override fun afterVisit(value: Jnode) {
            str += "\n"
            depth --
        }

        override fun afterVisit(value: Jobject) {
            str = str.dropLast(1) // delete trailing
            str += "\t".repeat(depth - 1) + "},\n"
            depth --
        }

        override fun afterVisit(value: Jarray) {
            str += "\t".repeat(depth - 1) + "],\n"
            depth --
        }

        override fun afterVisit(value: Jstring) {
        }

        override fun afterVisit(value: Jnumber) {
        }

        override fun afterVisit(value: Jbool) {
        }

        override fun afterVisit(value: Jnull) {
        }
    }

    init {
        // init window shell
        shell = Shell(Display.getDefault())
        shell.text = "Data model"
        shell.layout = GridLayout(2, false)

        var gridData = GridData()
        gridData.horizontalAlignment = GridData.FILL
        gridData.grabExcessHorizontalSpace = true


        // init tree widget
        tree = Tree(shell, SWT.SINGLE or SWT.BORDER)
        val treeGridData = GridData()
        treeGridData.verticalAlignment = GridData.FILL
        treeGridData.grabExcessVerticalSpace = true
        treeGridData.horizontalAlignment = GridData.FILL
        treeGridData.grabExcessHorizontalSpace = true
        tree.layoutData = treeGridData


        // replicate Jvalue data object to Tree
        val treeVisit = JvalueTreeVisitor()
        obj.accept(treeVisit)


        // init output text widget
        val textOut = Text(shell, SWT.WRAP or SWT.READ_ONLY or SWT.BORDER)
        val textOutgridData = GridData()
        textOutgridData.verticalAlignment = GridData.FILL
        textOutgridData.grabExcessVerticalSpace = true
        textOutgridData.horizontalAlignment = GridData.FILL
        textOutgridData.grabExcessHorizontalSpace = true
        textOut.layoutData = textOutgridData

        tree.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
//                println("selected: " + tree.selection.first().data)

                val jObj = tree.selection.first().data as Jvalue
                val tabVisitor = StringifyTabVisitor()
                jObj.accept(tabVisitor)
                textOut.text = tabVisitor.str
            }
        })

        // init input text widget
        val inputText = Text(shell, SWT.BORDER)
        inputText.message = "Type here to search"
        val inputGridData = GridData()
        inputGridData.horizontalAlignment = GridData.FILL
        inputGridData.grabExcessHorizontalSpace = true
        inputText.layoutData = inputGridData
        inputText.addModifyListener {
//            println(inputText.text.toString())
            tree.filter {
                inputText.text != null
                        && !inputText.text.equals("")
                        && inputText.text.length > 1
                        && it.text.contains(inputText.text)
            }
        }
    }

    // todo delete
    fun openTree() {
        tree.expandAll()
        shell.pack()
        shell.setSize(700, 700)
        shell.open()
        val display = Display.getDefault()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        display.dispose()
    }

    // helper functions

    fun Tree.expandAll() = traverse { it.expanded = true }

    fun Tree.collapseAll() = traverse {it.expanded = false}

    fun Tree.traverse(visitor: (TreeItem) -> Unit) {
        fun TreeItem.traverse() {
            visitor(this)
            items.forEach {
                it.traverse()
            }
        }
        items.forEach { it.traverse() }
    }

    //
    fun Tree.filter(meet: (TreeItem) -> Boolean) {
        fun TreeItem.walkaround() {
            if (meet(this)) {
                this.background = Color(RGB(0,255,255))
            } else {
                this.background = Color(RGBA(255,255,255,0))
            }
            items.forEach {
                it.walkaround()
            }
        }
        items.forEach { it.walkaround() }
    }
}

fun main() {

    var myArray = Jarray(mutableListOf<Jvalue>(
        Jstring("one"),
        Jstring("two"),
        Jstring("three")
    ))
    var myObj = Jobject(mutableListOf<Jnode>(
        Jnode("node01", Jstring("node value")),
        Jnode("node02", Jstring("node value")),
        Jnode("node03", Jstring("node value"))
    ))

    var myNode1 = Jnode("myNode1", myObj)
    var myNode2 = Jnode("myNode2", myArray)

    var rootObj = Jobject(mutableListOf<Jnode>(myNode1, myNode2))
    rootObj.addNode(Jnode("node04", Jnumber(5.0)))
    var rootArray = Jarray(mutableListOf<Jvalue>(myArray, myObj))
    rootArray.addValue(Jobject(mutableListOf(Jnode("node05", Jstring("node 05 value")))))
    rootObj.addNode(Jnode("node06", Jbool(true)))
    rootObj.addNode(Jnode("node07", Jnull()))

    var rootNode1 = Jnode(value = rootObj)


    val win = WindowTree(rootNode1)
    win.openTree()


}