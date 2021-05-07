package gson_gen

import Dummy
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

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

        var parent = tree

        fun getItem() {

        }

        override fun visit(value: Jnode) {

        }

        override fun visit(value: Jobject) {}

        override fun visit(value: Jarray) {}

        override fun visit(value: Jstring) {}

        override fun visit(value: Jnumber) {}

        override fun visit(value: Jbool) {}

        override fun visit(value: Jnull) {}

        override fun afterVisit(value: Jnode) {}

        override fun afterVisit(value: Jobject) {}

        override fun afterVisit(value: Jarray) {}

        override fun afterVisit(value: Jstring) {}

        override fun afterVisit(value: Jnumber) {}

        override fun afterVisit(value: Jbool) {}

        override fun afterVisit(value: Jnull) {}

    }

    init {
        // init window shell
        shell = Shell(Display.getDefault())
        shell.setSize(1000, 1000)
        shell.text = "File tree skeleton"
        shell.layout = GridLayout(2, false)

        // init tree widget
        tree = Tree(shell, SWT.SINGLE or SWT.BORDER)


        val treeVisit = JvalueTreeVisitor()
        obj.accept(treeVisit)


        // init output text widget
        val textOut = Text(shell, SWT.WRAP or SWT.READ_ONLY or SWT.BORDER)

        tree.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                println("selected: " + tree.selection.first().data) // todo add stringify function invocation
                textOut.text = tree.selection.first().data.toString()
            }
        })
        // init input text widget
        val inputText = Text(shell, SWT.BORDER)
        val gridData = GridData()
        gridData.horizontalAlignment = GridData.FILL
        gridData.grabExcessHorizontalSpace = true
        inputText.layoutData = gridData
        inputText.addModifyListener { println(inputText.text.toString()) } // todo add search function invocation

    }



    // todo delete
    fun openTree() {
        addTree(tree)
        shell.pack()
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
        jvalueToTree(rootNode)
        shell.pack()
        shell.open()
        val display = Display.getDefault()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        display.dispose()
    }

    private fun jvalueToTree(rootNode: Jvalue) {

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