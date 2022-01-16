import android.util.Log
import java.lang.Math.pow
import kotlin.math.*

/**
 * Здесь расположенны все отдельные методы алгоритма
 */

/**
 * Определение среднего значения:
На вход метода поступает чанк данных (в размере примерно 25),
задача на выходе получить среднее значение по этому чанку
 */
fun Mean_P(Data_Chunk : ArrayList<XYZ>) : ArrayList<Double> {
    // initialize first Res variable with first vector of chunk
    var Res = XYZ(Data_Chunk[0].x,Data_Chunk[0].y,Data_Chunk[0].z)

    var xSum = 0.0
    var ySum = 0.0
    var zSum = 0.0

    for (i in 0 until Data_Chunk.size){
        xSum += Data_Chunk[i].x
        ySum += Data_Chunk[i].y
        zSum += Data_Chunk[i].z
    }

    var  xAvrg = xSum / (Data_Chunk.size)
    var  yAvrg = ySum / (Data_Chunk.size)
    var  zAvrg = zSum / (Data_Chunk.size)

    //Log.i("prov","$xAvrg, $yAvrg, $zAvrg")
    return arrayListOf((xAvrg), (yAvrg), (zAvrg))

    //return arrayListOf(roundTo6decimials(xAvrg), roundTo6decimials(yAvrg), roundTo6decimials(zAvrg))
}

/**
 * Определение среднеквадратического отклонения:
 *
 * На вход метода поступает чанк данных
 * и среднее значение по этому чанку
 * (не обязательно, метод может обладать полиморфизмом и,
 * если среднего значения не поступает на вход, должен высчитывать его самостоятельно).
 * На выходе – вектор – значение среднеквадратического отклонения по поступившему чанку.
 */
// Mean 0.13420128	0.43437188	0.8938564  //
fun Deviation_P(Data_Chunk: ArrayList<XYZ>, Mean : ArrayList<Double>) : ArrayList<Double>{
    //Log.i("devi", "dchunk:${Data_Chunk.joinToString()} ${Mean.joinToString()}")
    //var Res = arrayListOf<Double>(0.0,0.0,0.0)
    var xSum = 0.0
    var ySum = 0.0
    var zSum = 0.0

    for (i in 0 until Data_Chunk.size) {
        xSum += (Data_Chunk[i].x - Mean[0]).pow(2.0)
        ySum += (Data_Chunk[i].y - Mean[1]).pow(2.0)
        zSum += (Data_Chunk[i].z - Mean[2]).pow(2.0)
    }

    xSum = Math.sqrt(xSum / (Data_Chunk.size-1))
    ySum = Math.sqrt(ySum / (Data_Chunk.size-1))
    zSum = Math.sqrt(zSum / (Data_Chunk.size-1))

    return arrayListOf<Double>(xSum,ySum,zSum)
}

fun calculateStandardDeviation_P(Data_Chunk: ArrayList<XYZ>): ArrayList<Double> {
    //int[] array;
    var x: ArrayList<Double> = ArrayList()
    var y: ArrayList<Double> = ArrayList()
    var z: ArrayList<Double> = ArrayList()

    for (i in 0 until Data_Chunk.size) {
        x.add(Data_Chunk[i].x)
        y.add(Data_Chunk[i].y)
        z.add(Data_Chunk[i].z)
    }


    // finding the sum of array values
    var Xsum = 0.0
    var Ysum = 0.0
    var Zsum = 0.0

    for (i in 0 until Data_Chunk.size) {
        Xsum += x[i]
        Ysum += y[i]
        Zsum += z[i]
    }

    // getting the mean of array.
    val meanX = Xsum / x.size
    val meanY = Ysum / y.size
    val meanZ = Zsum / z.size

    // calculating the standard deviation
    var standardDeviationX = 0.0
    var standardDeviationY = 0.0
    var standardDeviationZ = 0.0

    for (i in 0 until Data_Chunk.size) {
        standardDeviationX += (x[i] - meanX).pow(2.0)
        standardDeviationY += (y[i] - meanY).pow(2.0)
        standardDeviationZ += (z[i] - meanZ).pow(2.0)
    }

    return arrayListOf((sqrt(standardDeviationX / x.size)),(sqrt(standardDeviationY / y.size)),(sqrt(standardDeviationZ / z.size)) )
}


/**
 * Расчет нормы вектора:
 *
 * На вход поступает вектор, задача посчитать его «длину» или же норму.
 */

fun VectorNorm_P(Vector : ArrayList<Double>): Double {

    when(Vector.size){
        2 ->{return sqrt(Vector[0].pow(2.0) + Vector[1].pow(2.0))}
        3 ->{return sqrt(Vector[0].pow(2.0) + Vector[1].pow(2.0) + Vector[2].pow(2.0))}
        4 ->{return sqrt(Vector[0].pow(2.0) + Vector[1].pow(2.0) + Vector[2].pow(2.0) + Vector[3].pow(2.0))}

        else->{return sqrt(Vector[0].pow(2.0) + Vector[1].pow(2.0) + Vector[2].pow(2.0))}
    }


    //return Result
}

/**
 * Нормирование вектора:
 *
 *  На вход поступает вектор, задача привести его к нормированному виду
 *  (нормированный вид вектора имеет то же направление, что и изначальный,
 *  но при этом его длина равняется единице).
 */
fun NormVector_P(Vector: ArrayList<Double>): ArrayList<Double> {
    var sizeOfInputArray = Vector.size

    when(sizeOfInputArray){
        2 -> {
            var Result = arrayListOf<Double>(0.0,0.0)
            //var a = ArrayList<Double>(5)
            var Norm = VectorNorm_P(Vector)
            Result[0] = Vector[0] / Norm
            Result[1] = Vector[1] / Norm

            return Result
        }

        3 -> {
            var Result = arrayListOf<Double>(0.0,0.0,0.0)
            //var a = ArrayList<Double>(5)
            if (Vector[0] == 0.0 && Vector[1] == 0.0 && Vector[2] == 0.0){
                return arrayListOf(0.0,0.0,0.0)
            }
            var Norm = VectorNorm_P(Vector)
            Result[0] = Vector[0] / Norm
            Result[1] = Vector[1] / Norm
            Result[2] = Vector[2] / Norm


            return Result
        }

        4 -> {
            var Result = arrayListOf<Double>(0.0,0.0,0.0,0.0)
            //var a = ArrayList<Double>(5)
            if (Vector[0] == 0.0 && Vector[1] == 0.0 && Vector[2] == 0.0){
                return arrayListOf(0.0,0.0,0.0,0.0)
            }

            var Norm = VectorNorm_P(Vector)
            Result[0] = Vector[0] / Norm
            Result[1] = Vector[1] / Norm
            Result[2] = Vector[2] / Norm
            Result[3] = Vector[3] / Norm
            return Result
        }

        else -> {
            var Result = arrayListOf<Double>(0.0,0.0,0.0)
            //var a = ArrayList<Double>(5)
            var Norm = VectorNorm_P(Vector)
            Result[0] = Vector[0] / Norm
            Result[1] = Vector[1] / Norm
            Result[2] = Vector[2] / Norm
            return Result
        }
    }
}

/**
 * Скалярное произведение.
 Метод получает на вход два вектора, возвращает скалярное произведение двух векторов.
 */

fun DotProd_P(V1: ArrayList<Double>, V2 : ArrayList<Double>) : Double {
    when(V1.size){
        2-> return (V1[0]*V2[0] + V1[1]*V2[1])
        3-> return (V1[0]*V2[0] + V1[1]*V2[1] + V1[2]*V2[2])
        4-> return (V1[0]*V2[0] + V1[1]*V2[1] + V1[2]*V2[2] +V1[3]*V2[3])

        else -> return (V1[0]*V2[0] + V1[1]*V2[1] + V1[2]*V2[2])
    }


}

/**
 * Векторное произведение
Метод получает на вход два вектора, возвращает векторное произведение двух векторов.
 */

fun CrossProd_P(V1: ArrayList<Double>, V2: ArrayList<Double>) : ArrayList<Double>{
    return arrayListOf(
        (V1[1]*V2[2]-V1[2]*V2[1]),
        (V1[2]*V2[0]-V1[0]*V2[2]),
        (V1[0]*V2[1]-V1[1]*V2[0])
    )
}
/**
 * Расчёт поворотного вектора:
На вход подаются два вектора (внимание, для гарантии достоверного результата,
векторы лучше нормализовать заранее), на выходе четырехмерный вектор,
содержащий направление оси поворота (в первых трех элементах) и угла поворота (в четвертом).
 */
fun RotVecCalc_P(V1: ArrayList<Double>, V2: ArrayList<Double>): ArrayList<Double> {
    var RotVec : ArrayList<Double> = CrossProd_P(V1, V2)
    var Cosang = 0.0

    if ((VectorNorm_P(V1)*VectorNorm_P(V2)) == 0.0){
        Cosang = 0.0
    }else{
        Cosang = ( (DotProd_P(V1, V2))/(VectorNorm_P(V1)*VectorNorm_P(V2)) )
    }


    var Ang = acos(Cosang)
    RotVec.add(Ang)

    if (RotVec.size != 4){
        throw Exception("ALGO ERROR")
    }

    return RotVec
}

/**
 * Перевод поворотного вектора в кватернион:
На вход поступает четырехмерный поворотный вектор,
на выходе должен быть четырехмерный вектор кватерниона поворота.
 */
fun Axang2Q_P(RotVec : ArrayList<Double>): ArrayList<Double> {
    // Инициализировать сразу с нулями  ???
    var Q  = arrayListOf<Double>(0.0, 0.0, 0.0, 0.0)
    var RV = arrayListOf<Double>(0.0, 0.0, 0.0, 0.0)

    Q[0] = cos(RotVec[3]/2)

    RV[0] = RotVec[0]
    RV[1] = RotVec[1]
    RV[2] = RotVec[2]


    var V  = NormVector_P(RV)
    Q[1] = V[0] * sin(RotVec[3] / 2)
    Q[2] = V[1] * sin(RotVec[3] / 2)
    Q[3] = V[2] * sin(RotVec[3] / 2)

    //Q[1] = V [0] * sin(RotVec[3] / 2)
    //Q[2] = V [1] * sin(RotVec[3] / 2)
    //Q[3] = V [2] * sin(RotVec[3] / 2)

    return Q
}

/**
 * Перевод кватерниона в матрицу поворота:
Для перевода показаний измерителя в систему отсчета,
связанную с автомобилем, самый простой способ,
храня данные об ориентации в виде кватернионов, при вычислениях пользоваться матричным видом.
 */
fun Quat2DCM_P(Q : ArrayList<Double>): Array<Array<Double>> {
    var Qnew = NormVector_P(Q)
    var DCM = arrayOf<Array<Double>>(
        arrayOf(0.0,0.0,0.0),
        arrayOf(0.0,0.0,0.0),
        arrayOf(0.0,0.0,0.0)
    )


    DCM[0][0] = pow(Qnew[0],2.0)+ pow(Qnew[1],2.0) - pow(Qnew[2],2.0)-pow(Qnew[3],2.0)
    DCM[0][1] = 2 *(Qnew[1]*Qnew[2] + Qnew[0]*Qnew[3])
    DCM[0][2] = 2 *(Qnew[1]*Qnew[3] - Qnew[0]*Qnew[2])
    DCM[1][0] = 2 *(Qnew[1]*Qnew[2] - Qnew[0]*Qnew[3])
    DCM[1][1] = pow(Qnew[0],2.0) - pow(Qnew[1],2.0)+ pow(Qnew[2],2.0)-pow(Qnew[3],2.0)
    DCM[1][2] = 2 *(Qnew[2]*Qnew[3] + Qnew[0]*Qnew[1])
    DCM[2][0] = 2 *(Qnew[1]*Qnew[3] + Qnew[0]*Qnew[2])
    DCM[2][1] = 2 *(Qnew[2]*Qnew[3] - Qnew[0]*Qnew[1])
    DCM[2][2] = pow(Qnew[0],2.0)- pow(Qnew[1],2.0)-pow(Qnew[2],2.0)+pow(Qnew[3],2.0)

    return DCM
}
/**
 * Поворот вектора кватернионом:
При помощи этого метода, входной вектор записывается в другой системе координат.
На вход подается вектор, который следут повернуть и соответствующий кватернион поворота.
На выходе  - вектор, повернутый кватернионом.
 */
fun Quatrotate_P(Q : ArrayList<Double>, V : ArrayList<Double>): ArrayList<Double> {
    //Log.i("vvv","size"+Q.size+" || "+V.size)
    var DCM = Quat2DCM_P(Q)
    var Res = arrayListOf<Double>(0.0,0.0,0.0)

    Res[0] = DCM[0][0]*V[0] + DCM[0][1]*V[1] + DCM[0][2]*V[2]
    Res[1] = DCM[1][0]*V[0] + DCM[1][1]*V[1] + DCM[1][2]*V[2]
    Res[2] = DCM[2][0]*V[0] + DCM[2][1]*V[1] + DCM[2][2]*V[2]

    return Res
}

/**
 * Частное двух кватернионов:
Определяет операцию деления между двумя кватернионами (четырехмерные вектора).
На входе – два кватерниона, на выходе – результат деления между ними.
 */

fun Quatdivide_P(Q: ArrayList<Double>, R : ArrayList<Double>): ArrayList<Double> {
    var Norm = VectorNorm_P(R)
    // инициализировать с нулями ???
    var Res = arrayListOf<Double>(0.0,0.0,0.0,0.0)
    if(Norm!= 0.0){
        Res[0] = ((R[0]*Q[0] + R[1]*Q[1] + R[2]*Q[2] + R[3]*Q[3]) / Norm)
        Res[1] = ((R[0]*Q[1] + R[1]*Q[0] + R[2]*Q[3] + R[3]*Q[2]) / Norm)
        Res[2] = ((R[0]*Q[2] + R[1]*Q[3] + R[2]*Q[0] + R[3]*Q[1]) / Norm)
        Res[3] = ((R[0]*Q[3] + R[1]*Q[2] + R[2]*Q[1] + R[3]*Q[0]) / Norm)

    }else{
        Log.e("eee","NORM == 0!!!!!!!!!!")
        Res[0] = 1.0
        Res[0] = 0.0
        Res[0] = 0.0
        Res[0] = 0.0
    }



    //Res[0] = (R[0]*Q[0] + R[1]*Q[1] + R[2]*Q[2] + R[3]*Q[3]) / Norm
    //Res[1] = (R[0]*Q[1] + R[1]*Q[0] + R[2]*Q[3] + R[3]*Q[2]) / Norm
    //Res[2] = (R[0]*Q[2] + R[1]*Q[3] + R[2]*Q[0] + R[3]*Q[1]) / Norm
    //Res[3] = (R[0]*Q[3] + R[1]*Q[2] + R[2]*Q[1] + R[3]*Q[0]) / Norm
    return Res
}
/**
 * Нахождение угла между двумя кватернионами:
На вход поступают два кватерниона.
На выходе – угловая разница между результирующими поворотами, которые в этих кватернионах заложены.
 */
fun AngleBetweenQ_P(Q1 : ArrayList<Double>, Q2 : ArrayList<Double>): Double {
    var Q_d = Quatdivide_P(Q1,Q2)
    var Theta=0.0
    if(abs(Q_d[0])<1.0)
    {
        Theta = acos(Q_d[0]) * 2
    }
    else
    {
        Theta = acos(1.0)
    }
    return Math.toDegrees(Theta) // sic?
}

/**
 * Нахождение индекса максимального (по модулю) элемента в векторе:
На вход поступает вектор. На выходе – индекс максимального элемента в этом векторе.
 */
fun Find_max_index_P(V: ArrayList<Double>): Int {

    var Res = abs(V[0])
    var MAX_INDEX = 0
    //var prep = doubleArrayOf()

    for (i in 1 until V.size) {
        //Res = abs(V[i])

        if (abs(V[i]) > Res){  // !!! >= or > ?
            Res = abs(V[i])
            MAX_INDEX = i
        }
    }
    return MAX_INDEX
}

fun Quat2Axang_P(q : ArrayList<Double>) : ArrayList<Double>{
    var Axang = arrayListOf<Double>(0.0, 0.0, 0.0, 0.0)

    Axang[3] = 2 * acos(q[0])               // sic?
    var n_inv = 1.0 / sqrt(1.0-q[0]*q[0])

    Axang[0] = q[1] * n_inv
    Axang[1] = q[2] * n_inv
    Axang[2] = q[3] * n_inv

    return Axang
}

