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

        // helper workaround null pointer
//        fun Stack<Widget>.getStack(): TreeItem {
//            if (treeStack.stackObj.size > 0) {
//                return treeStack.read()
//            } else {
//                return tree
//            }
//        }

        // helper workaround null pointer
//        fun Stack<Widget>.pullStack(): TreeItem {
//            if (treeStack.stackObj.size > 0) {
//                return treeStack.pull()
//            } else {
//                return tree
//            }
//        }
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
            str = str.dropLast(1) // delete trailing comma
            str += "\t".repeat(depth - 1) + "},\n"
            depth --
        }

        override fun afterVisit(value: Jarray) {
//            str = str.dropLast(1) // delete trailing comma
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

//        textOut.text = "ersgherthsrsdth\n" +
//                "rthdrthdrthrdth\n" +
//                "drthdrthrdthdrth\n" +
//                "rtdhdrjjmntdgtjyjt\n" +
//                "dbdtyjdtry bjtjtjtdj\n" +
//                " yjdtyjdt jdt jdtyj dtyr\n" +
//                "dtyj dyj dtyj dtyj \n" +
//                "dtryj dtyjdtyj dtj\n"

        tree.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                println("selected: " + tree.selection.first().data) // todo add stringify function invocation

                val jObj = tree.selection.first().data as Jvalue
                val tabVisitor = StringifyTabVisitor()
                jObj.accept(tabVisitor)
                textOut.text = tabVisitor.str
            }
        })

        // init input text widget
        val inputText = Text(shell, SWT.BORDER)
        inputText.message = "lkdjl"
        val inputGridData = GridData()
        inputGridData.horizontalAlignment = GridData.FILL
        inputGridData.grabExcessHorizontalSpace = true
        inputText.layoutData = inputGridData
        inputText.addModifyListener {
            // todo add search function invocation
            println(inputText.text.toString())
            tree.filter {
                inputText.text != null && !inputText.text.equals("") && it.text.contains(inputText.text)
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

    fun addTree(tree: Tree) {
        val a = TreeItem(tree, SWT.NONE)
        a.text = "Alkjhl"
        a.data = Dummy(1)

        a.background = Color(RGB(23, 34,43))



        val b = TreeItem(tree, SWT.NONE)
        b.text = "Bdfgdf"
        b.data = Dummy(2)

        val b1 = TreeItem(b, SWT.NONE)
        b1.text = "b1vbncgfh"
        b1.data = Dummy(3)

        val b2 = TreeItem(b, SWT.NONE)
        b2.text = "b2vcnvcmn"
        b2.data = Dummy(4)

        val c = TreeItem(tree, SWT.NONE)
        c.text = "Cvccvnbmng"
        c.data = Dummy(5)

        val c1 = TreeItem(c, SWT.NONE)
        c1.text = "c1cvvncv"
        c1.data = Dummy(6)

        val c1a = TreeItem(c1, SWT.NONE)
        c1a.text = "c1acvbmn"
        c1a.data = Dummy(7)
    }

    fun Tree.addAsTreeItem(obj: Jnode) {
        val item = TreeItem(this, SWT.NONE)
        item.text = obj.key
        item.data = obj.value
    }

    fun open(rootNode: Jvalue) {
//        jvalueToTree(rootNode)
        shell.pack()
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