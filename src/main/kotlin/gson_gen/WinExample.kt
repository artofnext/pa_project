import org.eclipse.swt.SWT
import org.eclipse.swt.events.SelectionAdapter
import org.eclipse.swt.events.SelectionEvent
import org.eclipse.swt.layout.FillLayout
import org.eclipse.swt.layout.GridLayout
import org.eclipse.swt.widgets.*

fun main() {
    FileTreeSkeleton().open()
//    TODO("implement")
}

data class Dummy(val number: Int)

class FileTreeSkeleton() {
    val shell: Shell
    val tree: Tree

    init {
        shell = Shell(Display.getDefault())
        shell.setSize(1000, 1000)
        shell.text = "File tree skeleton"
        shell.layout = GridLayout(2,false)


        tree = Tree(shell, SWT.SINGLE or SWT.BORDER)

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

        tree.addSelectionListener(object : SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                println("selected: " + tree.selection.first().data)
            }
        })



        val text = Text(shell, SWT.WRAP)
        text.text = "public static void main(String[] args) {\n" +
                "\tDisplay display = new Display();\n" +
                "\tShell shell = new Shell(display);\n" +
                "\tshell.setText(\"Snippet 176\");\n" +
                "\tRowLayout layout = new RowLayout(SWT.HORIZONTAL);\n" +
                "\tlayout.wrap = true;\n" +
                "\tlayout.fill = false;\n" +
                "\tlayout.justify = true;\n" +
                "\tshell.setLayout(layout);\n"

//        text.setSize(1000, 1000)

//        val label = Label(shell, SWT.NONE)
//        label.text = "skeleton"

        val inputText = Text(shell, SWT.BORDER)

        inputText.addModifyListener { println(inputText.text.toString()) }



        val button = Button(shell, SWT.PUSH)
        button.text = "depth"
        button.addSelectionListener(object: SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                val item = tree.selection.first()
//                label.text = item.depth().toString()
            }
        })

    }

    // auxiliar para profundidade do nó
    fun TreeItem.depth(): Int =
        if(parentItem == null) 0
        else 1 + parentItem.depth()


    fun open() {

//        TODO("Implement"  )
        tree.expandAll()
        shell.pack()
        shell.open()
        val display = Display.getDefault()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        display.dispose()
    }
}


// auxiliares para varrer a árvore

fun Tree.expandAll() = traverse { it.expanded = true }

fun Tree.traverse(visitor: (TreeItem) -> Unit) {
    fun TreeItem.traverse() {
        visitor(this)
        items.forEach {
            it.traverse()
        }
    }
    items.forEach { it.traverse() }
}


