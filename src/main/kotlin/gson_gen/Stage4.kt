import gson_gen.*
import gson_gen.WindowTree
import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.graphics.Color
import org.eclipse.swt.graphics.RGB
import org.eclipse.swt.graphics.RGBA
import org.eclipse.swt.layout.GridData
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*
//import java.awt.Image
import java.awt.LayoutManager

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.*;

interface Appearance {
    val name: String
    val iconSet: String

}

class DefaultSetup : Appearance {
    override val name: String
        get() = "Test"
    override val iconSet: String
        get() = TODO("Not yet implemented")
//    override val layoutManager: LayoutManager
//        get() = java.awt.GridLayout(2, 1)
}

class WindowPlugTree(obj: Jvalue) {
    val shell: Shell
    var tree: Tree
    val display = Display()

    val iconSet: MutableMap<String, String> = mutableMapOf(
        "NODE_IMG" to "folder-icon-32.png",
        "LEAF_IMG" to "document-icon-32.png",
    )


    inner class JvalueTreeVisitor(): Visitor {

        // use stack for refer parent node
        var treeStack = Stack<TreeItem>()

        private fun getTreeItem(): TreeItem {

            val current: TreeItem
            if (treeStack.stackObj.size < 1) {
                current = TreeItem(tree, SWT.NONE)
//                current.image = Image(display, "folder-icon.png")
            } else {
                current = TreeItem(treeStack.read(), SWT.NONE)
//                current.setImage(Image(display, "folder-icon.png"))
            }
            return current
        }

        private fun TreeItem.appendIcon() {
            var jvalue = this.data as Jvalue
            if(jvalue.isNode) {
                this.image = Image(display, iconSet["NODE_IMG"])
            } else {
                this.image = Image(display, iconSet["LEAF_IMG"])
            }
        }

        override fun visit(value: Jnode) {
            val current = getTreeItem()
            current.text = value.key
            current.data = value.value
            current.appendIcon()
            treeStack.push(current)
        }

        override fun visit(value: Jobject) {
//            val current = getTreeItem()
//            current.text = "object"
//            current.data = value
//            current.appendIcon()
//            treeStack.push(current)
        }

        override fun visit(value: Jarray) {
//            val current = getTreeItem()
//            current.text = "array"
//            current.data = value
//            current.appendIcon()
//            treeStack.push(current)
        }

        override fun visit(value: Jstring) {
            val current = getTreeItem()
            current.text = "string: $value"
            current.data = value
//            current.appendIcon()
        }

        override fun visit(value: Jnumber) {
            val current = getTreeItem()
            current.text = "number: $value"
            current.data = value
//            current.appendIcon()
        }

        override fun visit(value: Jbool) {
            val current = getTreeItem()
            current.text = "bool: $value"
            current.data = value
//            current.appendIcon()
        }

        override fun visit(value: Jnull) {
            val current = getTreeItem()
            current.text = "null"
            current.data = Jstring("null")
//            current.appendIcon()
        }

        override fun afterVisit(value: Jnode) {
            if(treeStack.stackObj.size > 0) {
                treeStack.pull()
            }
        }

        override fun afterVisit(value: Jobject) {
            if(treeStack.stackObj.size > 0) {
//                treeStack.pull()
            }
        }

        override fun afterVisit(value: Jarray) {
            if(treeStack.stackObj.size > 0) {
//                treeStack.pull()
            }
        }
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
            depth --
            str += "\t".repeat(depth) + "},\n"
        }

        override fun afterVisit(value: Jarray) {
            depth --
            str += "\t".repeat(depth) + "],\n"
        }
    }

    init {
        // init window shell

        shell = Shell(display)
        shell.text = "Data model"
        shell.layout = GridLayout(2, false)
//        shell.image = display.getSystemImage(SWT.ICON_ERROR)



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

    fun openTree() {
        tree.expandAll()
//        tree.setIcons()
        shell.pack()
        shell.setSize(700, 700)
        shell.image = Image(display, "icon.png")
        shell.open()
        val display = Display.getDefault()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        display.dispose()
    }

    fun TreeItem.addIconSet() {
        this.image = Image(display, "document-icon-32.png")
    }

//    fun Tree.setIcons() = traverse {it.addIconSet()}

    // helper functions

    fun Tree.expandAll() = traverse { it.expanded = true }


//    fun Tree.icons() = traverse { it.image = Image(display, "C:\\Users\\Iryna\\Documents\\Masters courses\\Iscte_logo\\icon.png") }

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
        Jnode("item01", Jstring("Verba volant, scripta manent")),
        Jnode("item02", Jstring("Verba volant, scripta manent")),
        Jnode("item03", Jstring("Verba volant, scripta manent"))
    ))

    var myNode1 = Jnode("object01", myObj)
    var myNode2 = Jnode("array02", myArray)

    var rootObj = Jobject(mutableListOf<Jnode>(myNode1, myNode2))
    rootObj.addNode(Jnode("item04", Jnumber(5.0)))
    var rootArray = Jarray(mutableListOf<Jvalue>(myArray, myObj))
    rootArray.addValue(Jobject(mutableListOf(Jnode("item05", Jstring("node 05 value")))))
    rootObj.addNode(Jnode("item06", Jbool(true)))
    rootObj.addNode(Jnode("item07", Jnull()))

    var rootNode1 = Jnode(value = rootObj)

    val win = WindowPlugTree(rootNode1)
    win.openTree()
}