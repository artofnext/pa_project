package gson_gen

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

import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.events.MenuAdapter
import org.eclipse.swt.events.MenuEvent
import java.io.File
import java.util.*
import kotlin.reflect.KClass
import kotlin.reflect.KMutableProperty
import kotlin.reflect.full.createInstance
import kotlin.reflect.full.declaredMemberProperties
import kotlin.reflect.full.hasAnnotation
import kotlin.reflect.jvm.isAccessible


interface Action {
    val name: String
    fun exec(treeItem: TreeItem)
}

class Open: Action {
    override val name: String
        get() = "Open"

    override fun exec(treeItem: TreeItem) {
        val win = WindowTree(Jnode(value = (treeItem.data as Jvalue)))
        win.openTree()
    }

}

class Edit: Action {
    override val name: String
        get() = "Edit"

    override fun exec(treeItem: TreeItem) {
//        treeItem.text = "new text"
        val window = EditWindow(treeItem.text)
        window.open()


    }

}

interface Appearance {
    val name: String
    val iconSet: MutableMap<String, String>

}

class DefaultSetup : Appearance {
    override val name: String
        get() = "File_type_icons"
    override val iconSet: MutableMap<String, String>
        get() = mutableMapOf(
        "NODE_IMG" to "folder-icon-32.png",
        "LEAF_IMG" to "document-icon-32.png",
        )
}

class CustomSetup : Appearance {
    override val name: String
        get() = "File_type_icons"
    override val iconSet: MutableMap<String, String>
        get() = mutableMapOf(
            "NODE_IMG" to "files-icon-32.png",
            "LEAF_IMG" to "help-icon-32.png",
        )
}

@Target(AnnotationTarget.PROPERTY)
annotation class InjectAdd

@Target(AnnotationTarget.PROPERTY)
annotation class Inject

class EditWindow(val value: String) {
    val shell: Shell
    var edited: String = ""
    val display = Display.getDefault()

    init {
        // init window shell

        shell = Shell(display)
        shell.text = "Editor"
        shell.layout = GridLayout(2, false)
        shell.image = Image(display, "icon.png")


        val gridData = GridData()
        gridData.horizontalAlignment = GridData.FILL
        gridData.grabExcessHorizontalSpace = true

//        val messageText = Text(shell, SWT.BORDER)
//        messageText.text = "New name:"

        // text field
        val inputText = Text(shell, SWT.BORDER)

        // Button
        val button = Button(shell, SWT.PUSH)
        button.text = "OK"
        button.isEnabled = inputText.text.toString() != ""
        button.addSelectionListener(object: SelectionAdapter() {
            override fun widgetSelected(e: SelectionEvent) {
                edited = inputText.text
                println(edited)
                // TODO save value and close
//                shell.close()
            }
        })
        // init input text widget config
        inputText.message = value
        val inputGridData = GridData()
        inputGridData.horizontalAlignment = GridData.FILL
        inputGridData.grabExcessHorizontalSpace = true
        inputText.layoutData = inputGridData
        inputText.addModifyListener {
            println(inputText.text.toString())
            button.isEnabled = inputText.text.toString() != ""

        }

//        shell.addListener(SWT.Close) { event -> event.doit = false }

//        shell.setVisible(false)
    }

    fun open() {
        shell.pack()
        shell.setSize(350, 100)
        shell.open()
//        val display = Display.getDefault()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        display.dispose()
    }

}

class WindowPlugTree() {
    val obj: Jvalue
    val shell: Shell
    var tree: Tree
    val display = Display()

//    @Inject
//    val icons: Appearance = CustomSetup()
    @Inject
    lateinit var icons: Appearance

//    val actions: MutableList<Action> = mutableListOf<Action>(Open(), Edit())
    @InjectAdd
    lateinit var actions: MutableList<gson_gen.Action>

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

        private fun TreeItem.appendIcon() {
            var jvalue = this.data as Jvalue
            if(jvalue.isNode) {
                this.image = Image(display, icons.iconSet["NODE_IMG"])
            } else {
                this.image = Image(display, icons.iconSet["LEAF_IMG"])
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
        // TODO workaround parameter passing ===============================

        fun createJvalueInstance(): Jnode {
            var myArray = Jarray(
                mutableListOf<Jvalue>(
                    Jstring("one"),
                    Jstring("two"),
                    Jstring("three")
                )
            )
            var myObj = Jobject(
                mutableListOf<Jnode>(
                    Jnode("item01", Jstring("Verba volant, scripta manent")),
                    Jnode("item02", Jstring("Verba volant, scripta manent")),
                    Jnode("item03", Jstring("Verba volant, scripta manent"))
                )
            )

            var myNode1 = Jnode("object01", myObj)
            var myNode2 = Jnode("array02", myArray)

            var rootObj = Jobject(mutableListOf<Jnode>(myNode1, myNode2))
            rootObj.addNode(Jnode("item04", Jnumber(5.0)))
            rootObj.addNode(Jnode("item06", Jbool(true)))
            rootObj.addNode(Jnode("item07", Jnull()))

            var rootNode1 = Jnode(value = rootObj)

            return rootNode1
        }

        obj = createJvalueInstance()

        // TODO end ========================================================
        // init window shell

        shell = Shell(display)
        shell.text = "Data model visualisation"
        shell.layout = GridLayout(2, false)
//        shell.image = display.getSystemImage(SWT.ICON_ERROR)

        val gridData = GridData()
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

    fun addMenu() {
        // right-click menu
        val menu = Menu(tree)
        tree.menu = menu
        menu.addMenuListener(object : MenuAdapter() {
            override fun menuShown(e: MenuEvent?) {
                val items = menu.items
                for (i in items.indices) {
                    items[i].dispose()
                }
                println("actions invoked")
                actions.forEach {

                    val newItem = MenuItem(menu, SWT.NONE)
//                newItem.text = "Menu for " + tree.selection[0].text
                    newItem.text = it.name
                    newItem.addSelectionListener(object: SelectionAdapter() {
                        override fun widgetSelected(e: SelectionEvent) {
                            println("selected: " + tree.selection.first().data)
                            it.exec(tree.selection.first())
                        }
                    })
                }
            }
        })

    }

    fun openTree() {
        // replicate Jvalue data object to Tree
        val treeVisit = JvalueTreeVisitor()
        obj.accept(treeVisit)
        this.addMenu()
        tree.expandAll()
//        tree.setIcons()
        shell.pack()
        shell.setSize(1000, 700)
        shell.image = Image(display, "icon.png")
        shell.open()
//        val display = Display.getDefault()
        while (!shell.isDisposed) {
            if (!display.readAndDispatch()) display.sleep()
        }
        display.dispose()
    }

//    fun TreeItem.addIconSet() {
//        this.image = Image(display, "document-icon-32.png")
//    }

//    fun Tree.setIcons() = traverse {it.addIconSet()}

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

class Injector {
    companion object {
        val map: MutableMap<String, List<KClass<*>>> = mutableMapOf()

        init {
            val scanner = Scanner(File("dependency.properties"))
            while (scanner.hasNextLine()) {
                val line = scanner.nextLine()
                val parts = line.split("=")
                //parts[1].split(",").map { Class.forName(parts[1]).kotlin }
                map[parts[0]] = parts[1].split(",")
                    .map { Class.forName(it).kotlin }
                //    .toMutableList()
            }
//            println(map)
            scanner.close()
        }

        fun<T:Any> create(type: KClass<T>): T {
//            println("before instance creation")
            val instance = type.createInstance() //
            println("instance created")

//            val instance = type.constructors.first().call(jvalueObj)

            type.declaredMemberProperties.forEach { it ->
                if(it.hasAnnotation<Inject>()) {
//                    it.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    val obj = map[key]!!.first().createInstance()
//                    println(key)
                    (it as KMutableProperty<*>).setter.call(instance, obj)
                }
                else if(it.hasAnnotation<InjectAdd>()) {
//                    it.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    val objs = map[key]!!.map { it.createInstance() }
                    (it as KMutableProperty<*>).setter.call(instance, objs)
                }
            }
            return instance
        }
    }}


class Injector1 {

    companion object{
        private const val configurationFilePathName: String = "dependency.properties"
        private val mutableMap : MutableMap<String, List<KClass<*>>> = mutableMapOf()

        init {
            val scanner = Scanner(File(configurationFilePathName))
            while (scanner.hasNextLine()){
                val line = scanner.nextLine()
                val parts = line.split("=")
                mutableMap[parts[0]] = parts[1].split(",")
                    .map{ Class.forName(it).kotlin }
            }
            scanner.close()
        }

        fun <T: Any> create(type: KClass<T>): T{
            val o = type.createInstance()
            type.declaredMemberProperties.forEach { it ->
                if(it.hasAnnotation<Inject>()){
                    it.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    val obj = mutableMap[key]!!.first().createInstance()
                    (it as KMutableProperty<*>).setter.call(o, obj)
                }else if(it.hasAnnotation<InjectAdd>()){
                    it.isAccessible = true
                    val key = type.simpleName + "." + it.name
                    val obj = mutableMap[key]!!.map { it.createInstance() }
                    (it.getter.call(o) as MutableList<Any>).addAll(obj)
                }
            }
            return o
        }
    }
}


fun main() {

    fun createJvalueInstance(): Jnode {
        var myArray = Jarray(
            mutableListOf<Jvalue>(
                Jstring("one"),
                Jstring("two"),
                Jstring("three")
            )
        )
        var myObj = Jobject(
            mutableListOf<Jnode>(
                Jnode("item01", Jstring("Verba volant, scripta manent")),
                Jnode("item02", Jstring("Verba volant, scripta manent")),
                Jnode("item03", Jstring("Verba volant, scripta manent"))
            )
        )

        var myNode1 = Jnode("object01", myObj)
        var myNode2 = Jnode("array02", myArray)

        var rootObj = Jobject(mutableListOf<Jnode>(myNode1, myNode2))
        rootObj.addNode(Jnode("item04", Jnumber(5.0)))
        rootObj.addNode(Jnode("item06", Jbool(true)))
        rootObj.addNode(Jnode("item07", Jnull()))

        var rootNode1 = Jnode(value = rootObj)

        return rootNode1
    }

//    val win = gson_gen.WindowPlugTree()

    val win = Injector.create(WindowPlugTree::class)

    win.openTree()
}