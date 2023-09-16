package com.hfad.mychronometr

// не забыть заимпортировать все компоненты
import androidx.appcompat.app.AppCompatActivity
import android.os.Bundle
import android.os.SystemClock
import android.widget.Button
import android.widget.Chronometer

class MainActivity : AppCompatActivity() {

    // объявляем переменные и присваиваем им начальные значения
    lateinit var myChronometer: Chronometer // в переменную myChronometr определил сам секундомер, но пока не сослались на него через id в xml макете
    var running = false // проверка на то запущен секундомер или нет
    var offset: Long = 0 // время на секундомере. Long - означает длинное число

    // создам bundle для сохранения ключ-значения при onDestroy активности и ее повторном onCreate()
    // для этого необходимо добавить по ключу для каждого нужного по смыслу приложения значению
    // под словом "ключ" имеется введу понимание как в объекте ключ/значение, через который я буду получать значение в bundle в savedInstanceState.getLong или getBoolean и тд
    val OFFSET_KEY = "offset" // время на секундомере, ключ "offset". начальные значения - ("offset": 0)
    val RUNNING_KEY = "running" // проверка на то запущен секундомер или нет, ключ "running". начальные значения  ("running" : false)
    val BASE_KEY = "base" // свойство, определяющее базовое время, ключ "base". начальные значения (хуй их знает. надо разобраться. скорее всего значение - начало отсчета (00:00), ключ - скорее всего сам base)

    // запускается активность
    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)

        myChronometer = findViewById<Chronometer>(R.id.myChronometer) // получил ссылку на сам секундомер (Chronometer)

        // сохраним стэйт при смене ориентации и перезапуске активности
        // savedInstanceState приходит аргументом выше в сам метод onCreate()
        // первый раз savedInstanceState = null, по этому необходимо через условие выловить != null
        if (savedInstanceState != null)  {
            // получим данные в bundle, который является аналогом json
            offset = savedInstanceState.getLong(OFFSET_KEY) // получил значение секундомера через ключ OFFSET_KEY методом getLong
            running = savedInstanceState.getBoolean(RUNNING_KEY) // получил значение запущен секундомер или нет через ключ RUNNING_KEY методом getBoolean
            // если секундомер запущен
            if (running) {
                myChronometer.base = savedInstanceState.getLong(BASE_KEY) // base - типа "база", основное время, которое было последний раз на секундомере, независимо от того запущен он или нет. короче те цифры, которые я вижу сейчас на секундомере - base
                myChronometer.start() // запусти секундомер при смене ориентации экрана (т.е. при onDestroy() и повторном цикле onCreate())
            } else setBaseTime() // если секундомер не запущен, выполни написанную мною функцию, которая просто обнуляет время
        }

        val startButton = findViewById<Button>(R.id.start) // получил ссылку на кнопку START в макете xml

        startButton.setOnClickListener { // прописал логику при клике на START
            if (!running) {
                setBaseTime() // установил время
                myChronometer.start() // метод start() идет из библиотеки хронометра, запускает секундомер
                running = true
            }
        }

        val pauseButton = findViewById<Button>(R.id.pause) // получил ссылку на кнопку PAUSE в макете xml

        pauseButton.setOnClickListener {        // прописал логику при клике на PAUSE
            if (running) {
                saveOffSet() // сохранил текущее значение секундомера
                myChronometer.stop() // метод stop() идет из библиотеки хронометра, останавливает секундомер
                running = false
            }
        }

        val resetButton = findViewById<Button>(R.id.reset) // получил ссылку на кнопку reset

        resetButton.setOnClickListener{
            offset = 0
            setBaseTime() // обнулил секундомер
        }

    }

    // функция, которая сохраняет стэйты, метод put кладет объекты в savedInstanceState: Bundle, или проще говоря, в Bundle
    // затем выше в коде через условие != null мы получаем значение числа перезапуска и активности и берем от сюда последнее сохраненнное значение
    override fun onSaveInstanceState(savedInstanceState: Bundle) {
        savedInstanceState.putLong(OFFSET_KEY, offset) // положи в Bundle длинное число с ключом OFFSET_KEY, значением offset (offset - время на секундомере!!!! пауза, не пауза, не важно.)
        savedInstanceState.putBoolean(RUNNING_KEY, running) // положи в Bundle булево значение с ключом RUNNING_KEY, значением true/false ("running": true) или ("running": true), по ситуации
        savedInstanceState.putLong(BASE_KEY, myChronometer.base) // положи в Bundle длинное число с ключом BASE_KEY и обнуленным временем
        super.onSaveInstanceState(savedInstanceState) // суперконструктор, как и в js, не надо вникать в эту строчку. просто принять её как данность. но она вроде в момент onDestroy сохраняет данные в bundle
    }

    // base - свойство, которое определяет базовое время, т.е. время, с которого начинается отсчет
    // SystemClock.elapsedRealTime() - возвращает время в милисекундах с момента загрузки устройства
    // присвоение этого метода свойству base - обнулит время

    // функция обнуления времени. .base, как и SystemClock.elapsedRealTime() являются библиотечными свойством и методом
    fun setBaseTime() {
        myChronometer.base = SystemClock.elapsedRealtime() - offset
    }

    // функция сохранения значения времени на секундомере
    fun saveOffSet() {
        offset = SystemClock.elapsedRealtime() - myChronometer.base
    }
}