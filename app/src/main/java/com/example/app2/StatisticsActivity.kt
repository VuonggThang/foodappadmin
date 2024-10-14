//package com.example.app2
//
//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.github.mikephil.charting.charts.BarChart
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.data.BarData
//import com.github.mikephil.charting.data.BarDataSet
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.example.app2.databinding.ActivityStatisticsBinding
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.data.BarEntry
//import java.util.*

//class StatisticsActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityStatisticsBinding
//    private lateinit var database: DatabaseReference
//
//    // Khai báo danh sách để lưu dữ liệu cho biểu đồ
//    private val ordersEntries = ArrayList<Entry>()
//    private val revenueEntries = ArrayList<BarEntry>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Khởi tạo View Binding
//        binding = ActivityStatisticsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Khởi tạo Firebase Database
//        database = FirebaseDatabase.getInstance().reference
//
//        // Khởi tạo biểu đồ
//        setupCharts()
//
//        // Thiết lập sự kiện cho nút chọn ngày
//        binding.selectDateButton.setOnClickListener {
//            showDatePickerDialog()
//        }
//        binding.backButton.setOnClickListener {
//            finish()
//        }
//
//        // Cập nhật thông tin ban đầu
//        updateStatistics(0, 0.0)
//    }
//
//    private fun setupCharts() {
//        // Thiết lập biểu đồ đơn hàng
//        binding.ordersLineChart.description.isEnabled = false
//        binding.ordersLineChart.setDrawGridBackground(false)
//
//        // Thiết lập biểu đồ doanh thu
//        binding.revenueBarChart.description.isEnabled = false
//        binding.revenueBarChart.setDrawGridBackground(false)
//    }
//
//    private fun showDatePickerDialog() {
//        // Lấy ngày hiện tại
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        // Tạo DatePickerDialog
//        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
//            val selectedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
//            // Hiển thị ngày đã chọn
//            binding.selectDateButton.text = selectedDate
//            // Cập nhật thống kê theo ngày đã chọn
//            updateStatisticsForSelectedDate(selectedYear, selectedMonth + 1, selectedDay)
//        }, year, month, day)
//
//        // Hiển thị DatePickerDialog
//        datePickerDialog.show()
//    }
//
//    private fun updateStatisticsForSelectedDate(year: Int, month: Int, day: Int) {
//        val selectedDate = "$year-$month-$day"
//        database.child("CompletedOrder").orderByChild("currentTime")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    var totalOrders = 0
//                    var totalRevenue = 0.0
//                    ordersEntries.clear() // Xóa dữ liệu cũ
//                    revenueEntries.clear() // Xóa dữ liệu cũ
//
//                    var entryIndex = 0
//
//                    for (orderSnapshot in snapshot.children) {
//                        val currentTime = orderSnapshot.child("currentTime").getValue(Long::class.java) ?: continue
//                        // Chuyển đổi timestamp sang ngày
//                        val orderDate = convertTimestampToDate(currentTime)
//
//                        if (orderDate == selectedDate) {
//                            totalOrders++
//                            val totalPrice = orderSnapshot.child("totalPrice").getValue(String::class.java)
//                                ?.replace("$", "")?.toDoubleOrNull() ?: 0.0
//                            totalRevenue += totalPrice
//
//                            // Thêm dữ liệu vào danh sách biểu đồ
//                            ordersEntries.add(Entry(entryIndex.toFloat(), totalOrders.toFloat()))
//                            revenueEntries.add(BarEntry(entryIndex.toFloat(), totalRevenue.toFloat()))
//                            entryIndex++
//                        }
//                    }
//
//                    // Cập nhật thống kê
//                    updateStatistics(totalOrders, totalRevenue)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("StatisticsActivity", "Error fetching data: ${error.message}")
//                }
//            })
//    }
//
//    private fun convertTimestampToDate(timestamp: Long): String {
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = timestamp
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//        val month = calendar.get(Calendar.MONTH) + 1 // Tháng bắt đầu từ 0
//        val year = calendar.get(Calendar.YEAR)
//        return "$year-$month-$day"
//    }
//
//    private fun updateStatistics(totalOrders: Int, totalRevenue: Double) {
//        // Cập nhật tổng đơn hàng
//        binding.totalOrdersTextView.text = totalOrders.toString()
//        // Cập nhật tổng doanh thu
//        binding.totalRevenueTextView.text = "$$totalRevenue"
//
//        // Cập nhật biểu đồ
//        updateCharts()
//    }
//
//    private fun updateCharts() {
//        // Cập nhật biểu đồ đơn hàng
//        val ordersDataSet = LineDataSet(ordersEntries, "Tổng đơn hàng")
//        ordersDataSet.color = resources.getColor(R.color.blue) // Đặt màu sắc cho đường biểu đồ
//        val lineData = LineData(ordersDataSet)
//        binding.ordersLineChart.data = lineData
//
//        // Cập nhật biểu đồ doanh thu
//        val revenueDataSet = BarDataSet(revenueEntries, "Tổng doanh thu")
//        revenueDataSet.color = resources.getColor(R.color.red) // Đặt màu sắc cho biểu đồ cột
//        val barData = BarData(revenueDataSet)
//        binding.revenueBarChart.data = barData
//
//        // Cập nhật biểu đồ
//        binding.ordersLineChart.invalidate() // Cập nhật biểu đồ đơn hàng
//        binding.revenueBarChart.invalidate() // Cập nhật biểu đồ doanh thu
//    }
//}


//package com.example.app2
//
//import android.app.DatePickerDialog
//import android.os.Bundle
//import android.util.Log
//import androidx.appcompat.app.AppCompatActivity
//import androidx.core.content.ContextCompat
//import com.github.mikephil.charting.charts.LineChart
//import com.github.mikephil.charting.data.Entry
//import com.github.mikephil.charting.data.LineData
//import com.github.mikephil.charting.data.LineDataSet
//import com.google.firebase.database.DataSnapshot
//import com.google.firebase.database.DatabaseError
//import com.google.firebase.database.DatabaseReference
//import com.google.firebase.database.FirebaseDatabase
//import com.google.firebase.database.ValueEventListener
//import com.example.app2.databinding.ActivityStatisticsBinding
//import com.github.mikephil.charting.components.AxisBase
//import com.github.mikephil.charting.components.XAxis
//import com.github.mikephil.charting.formatter.ValueFormatter
//import java.util.*
//
//class StatisticsActivity : AppCompatActivity() {
//
//    private lateinit var binding: ActivityStatisticsBinding
//    private lateinit var database: DatabaseReference
//
//    // Khai báo danh sách để lưu dữ liệu cho biểu đồ
//    private val ordersEntries = ArrayList<Entry>()
//
//    override fun onCreate(savedInstanceState: Bundle?) {
//        super.onCreate(savedInstanceState)
//
//        // Khởi tạo View Binding
//        binding = ActivityStatisticsBinding.inflate(layoutInflater)
//        setContentView(binding.root)
//
//        // Khởi tạo Firebase Database
//        database = FirebaseDatabase.getInstance().reference
//
//        // Khởi tạo biểu đồ
//        setupCharts()
//
//        // Thiết lập sự kiện cho nút chọn ngày
//        binding.selectDateButton.setOnClickListener {
//            showDatePickerDialog()
//        }
//
//        // Cập nhật thông tin ban đầu
//        updateStatisticsForCurrentDate() // Cập nhật thống kê theo ngày hiện tại
//    }
//
//    private fun setupCharts() {
//        // Thiết lập biểu đồ đơn hàng
//        binding.ordersLineChart.description.isEnabled = false
//        binding.ordersLineChart.setDrawGridBackground(false)
//
//        // Thiết lập trục Y cho biểu đồ
//        binding.ordersLineChart.axisLeft.axisMinimum = 0f // Giá trị tối thiểu trục Y
//        binding.ordersLineChart.axisRight.isEnabled = false // Tắt trục Y bên phải
//        binding.ordersLineChart.xAxis.position = XAxis.XAxisPosition.BOTTOM // Đặt vị trí trục X
//        binding.ordersLineChart.xAxis.granularity = 1f // Khoảng cách giữa các điểm trên trục X
//
//        // Thiết lập giá trị cho trục X
//        binding.ordersLineChart.xAxis.valueFormatter = object : ValueFormatter() {
//            override fun getAxisLabel(value: Float, axis: AxisBase?): String {
//                return "${value.toInt()}" // Chuyển giá trị trục X thành chuỗi
//            }
//        }
//    }
//
//    private fun showDatePickerDialog() {
//        // Lấy ngày hiện tại
//        val calendar = Calendar.getInstance()
//        val year = calendar.get(Calendar.YEAR)
//        val month = calendar.get(Calendar.MONTH)
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//
//        // Tạo DatePickerDialog
//        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
//            val selectedDate = "${selectedYear}-${selectedMonth + 1}-${selectedDay}"
//            // Hiển thị ngày đã chọn
//            binding.selectDateButton.text = selectedDate
//            // Cập nhật thống kê theo ngày đã chọn
//            updateStatisticsForSelectedDate(selectedYear, selectedMonth + 1, selectedDay)
//        }, year, month, day)
//
//        // Hiển thị DatePickerDialog
//        datePickerDialog.show()
//    }
//
//    private fun updateStatisticsForCurrentDate() {
//        val calendar = Calendar.getInstance()
//        updateStatisticsForSelectedDate(calendar.get(Calendar.YEAR), calendar.get(Calendar.MONTH) + 1, calendar.get(Calendar.DAY_OF_MONTH))
//    }
//
//    private fun updateStatisticsForSelectedDate(year: Int, month: Int, day: Int) {
//        val selectedDate = "$year-$month-$day"
//        val minuteOrders = IntArray(60) { 0 } // Tạo mảng đếm đơn hàng theo phút
//
//        database.child("CompletedOrder").orderByChild("currentTime")
//            .addListenerForSingleValueEvent(object : ValueEventListener {
//                override fun onDataChange(snapshot: DataSnapshot) {
//                    for (orderSnapshot in snapshot.children) {
//                        val currentTime = orderSnapshot.child("currentTime").getValue(Long::class.java) ?: continue
//                        val orderDate = convertTimestampToDate(currentTime)
//
//                        if (orderDate == selectedDate) {
//                            // Lấy phút từ timestamp
//                            val calendar = Calendar.getInstance().apply {
//                                timeInMillis = currentTime
//                            }
//                            val minute = calendar.get(Calendar.MINUTE)
//                            minuteOrders[minute]++ // Tăng số lượng đơn hàng cho phút tương ứng
//                        }
//                    }
//
//                    // Cập nhật thống kê
//                    updateStatistics(minuteOrders)
//                }
//
//                override fun onCancelled(error: DatabaseError) {
//                    Log.e("StatisticsActivity", "Error fetching data: ${error.message}")
//                }
//            })
//    }
//
//    private fun convertTimestampToDate(timestamp: Long): String {
//        val calendar = Calendar.getInstance()
//        calendar.timeInMillis = timestamp
//        val day = calendar.get(Calendar.DAY_OF_MONTH)
//        val month = calendar.get(Calendar.MONTH) + 1 // Tháng bắt đầu từ 0
//        val year = calendar.get(Calendar.YEAR)
//        return "$year-$month-$day"
//    }
//
//    private fun updateStatistics(minuteOrders: IntArray) {
//        // Cập nhật tổng đơn hàng
//        val totalOrders = minuteOrders.sum()
//        binding.totalOrdersTextView.text = totalOrders.toString()
//
//        // Cập nhật biểu đồ
//        updateCharts(minuteOrders)
//    }
//
//    private fun updateCharts(minuteOrders: IntArray) {
//        // Khởi tạo các giá trị cho biểu đồ
//        ordersEntries.clear() // Xóa dữ liệu cũ
//        for (minute in minuteOrders.indices) {
//            ordersEntries.add(Entry(minute.toFloat(), minuteOrders[minute].toFloat())) // Thêm dữ liệu theo phút
//        }
//
//        // Tạo dữ liệu cho biểu đồ
//        val dataSet = LineDataSet(ordersEntries, "Orders per Minute") // Đặt tên cho đường biểu đồ
//        dataSet.color = ContextCompat.getColor(this, R.color.blue) // Màu đường biểu đồ
//        dataSet.valueTextColor = ContextCompat.getColor(this, R.color.black) // Màu chữ cho giá trị
//
//        // Tạo dữ liệu cho biểu đồ đường
//        val lineData = LineData(dataSet)
//        binding.ordersLineChart.data = lineData
//        binding.ordersLineChart.invalidate() // Cập nhật biểu đồ
//    }
//}
package com.example.app2

import android.app.DatePickerDialog
import android.os.Bundle
import android.util.Log
import androidx.appcompat.app.AppCompatActivity
import androidx.core.content.ContextCompat
import com.github.mikephil.charting.charts.BarChart
import com.github.mikephil.charting.charts.LineChart
import com.github.mikephil.charting.data.BarData
import com.github.mikephil.charting.data.BarDataSet
import com.github.mikephil.charting.data.Entry
import com.github.mikephil.charting.data.LineData
import com.github.mikephil.charting.data.LineDataSet
import com.google.firebase.database.DataSnapshot
import com.google.firebase.database.DatabaseError
import com.google.firebase.database.DatabaseReference
import com.google.firebase.database.FirebaseDatabase
import com.google.firebase.database.ValueEventListener
import com.example.app2.databinding.ActivityStatisticsBinding
import com.github.mikephil.charting.components.XAxis
import com.github.mikephil.charting.data.BarEntry
import java.util.*

class StatisticsActivity : AppCompatActivity() {

    private lateinit var binding: ActivityStatisticsBinding
    private lateinit var database: DatabaseReference

    // Khai báo danh sách để lưu dữ liệu cho biểu đồ
    private val ordersEntries = ArrayList<Entry>()
    private val revenueEntries = ArrayList<BarEntry>()

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // Khởi tạo View Binding
        binding = ActivityStatisticsBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // Khởi tạo Firebase Database
        database = FirebaseDatabase.getInstance().reference

        // Khởi tạo biểu đồ
        setupCharts()

        // Thiết lập sự kiện cho nút chọn ngày
        binding.selectDateButton.setOnClickListener {
            showDatePickerDialog()
        }
        binding.backButton.setOnClickListener {
            finish()
        }

        // Cập nhật thông tin ban đầu
        updateStatistics(0, 0.0)
    }

    private fun setupCharts() {
        // Thiết lập biểu đồ đơn hàng
        binding.ordersLineChart.description.isEnabled = false
        binding.ordersLineChart.setDrawGridBackground(false) // Tắt lưới nền
        binding.ordersLineChart.setDrawBorders(false) // Tắt viền

        // Thiết lập lưới X
        val xAxis = binding.ordersLineChart.xAxis
        xAxis.setDrawGridLines(false) // Tắt lưới trên trục X
        xAxis.setDrawLabels(true) // Hiển thị nhãn trục X

        // Thiết lập biểu đồ doanh thu
        binding.revenueBarChart.description.isEnabled = false
        binding.revenueBarChart.setDrawGridBackground(false) // Tắt lưới nền
        binding.revenueBarChart.setDrawBorders(false) // Tắt viền

        // Thiết lập lưới X
        val revenueXAxis = binding.revenueBarChart.xAxis
        revenueXAxis.setDrawGridLines(false) // Tắt lưới trên trục X
        revenueXAxis.setDrawLabels(true) // Hiển thị nhãn trục X
    }

    private fun showDatePickerDialog() {
        // Lấy ngày hiện tại
        val calendar = Calendar.getInstance()
        val year = calendar.get(Calendar.YEAR)
        val month = calendar.get(Calendar.MONTH)
        val day = calendar.get(Calendar.DAY_OF_MONTH)

        // Tạo DatePickerDialog
        val datePickerDialog = DatePickerDialog(this, { _, selectedYear, selectedMonth, selectedDay ->
            val selectedDate = "${selectedDay}/${selectedMonth + 1}/$selectedYear"
            // Hiển thị ngày đã chọn
            binding.selectDateButton.text = selectedDate
            // Cập nhật thống kê theo ngày đã chọn
            updateStatisticsForSelectedDate(selectedYear, selectedMonth + 1, selectedDay)
        }, year, month, day)

        // Hiển thị DatePickerDialog
        datePickerDialog.show()
    }

    private fun updateStatisticsForSelectedDate(year: Int, month: Int, day: Int) {
        val selectedDate = "$year-$month-$day"
        database.child("CompletedOrder").orderByChild("currentTime")
            .addListenerForSingleValueEvent(object : ValueEventListener {
                override fun onDataChange(snapshot: DataSnapshot) {
                    var totalOrders = 0
                    var totalRevenue = 0.0
                    ordersEntries.clear() // Xóa dữ liệu cũ
                    revenueEntries.clear() // Xóa dữ liệu cũ

                    var entryIndex = 0

                    for (orderSnapshot in snapshot.children) {
                        val currentTime = orderSnapshot.child("currentTime").getValue(Long::class.java) ?: continue
                        // Chuyển đổi timestamp sang ngày
                        val orderDate = convertTimestampToDate(currentTime)

                        if (orderDate == selectedDate) {
                            totalOrders++
                            val totalPrice = orderSnapshot.child("totalPrice").getValue(String::class.java)
                                ?.replace("$", "")?.toDoubleOrNull() ?: 0.0
                            totalRevenue += totalPrice

//                            // Lấy foodQuantities
//                            val foodQuantities = orderSnapshot.child("foodQuantities").children.mapNotNull { it.getValue(Int::class.java) }
//
//                            // Thêm dữ liệu vào danh sách biểu đồ
//                            for (quantity in foodQuantities) {
//                                ordersEntries.add(Entry(entryIndex.toFloat(), quantity.toFloat()))
//                                entryIndex++
//                            }


                            // Thêm dữ liệu vào danh sách biểu đồ
                            ordersEntries.add(Entry(entryIndex.toFloat(), totalOrders.toFloat()))

                            // Thêm giá trị đơn hàng vào danh sách doanh thu
                            revenueEntries.add(BarEntry(entryIndex.toFloat(), totalPrice.toFloat()))
                            entryIndex++
                        }
                    }

                    // Cập nhật thống kê
                    updateStatistics(totalOrders, totalRevenue)
                }

                override fun onCancelled(error: DatabaseError) {
                    Log.e("StatisticsActivity", "Error fetching data: ${error.message}")
                }
            })
    }

    private fun convertTimestampToDate(timestamp: Long): String {
        val calendar = Calendar.getInstance()
        calendar.timeInMillis = timestamp
        val day = calendar.get(Calendar.DAY_OF_MONTH)
        val month = calendar.get(Calendar.MONTH) + 1 // Tháng bắt đầu từ 0
        val year = calendar.get(Calendar.YEAR)
        return "$year-$month-$day"
    }

    private fun updateStatistics(totalOrders: Int, totalRevenue: Double) {
        // Cập nhật tổng đơn hàng
        binding.totalOrdersTextView.text = totalOrders.toString()
        // Cập nhật tổng doanh thu
        binding.totalRevenueTextView.text = "$$totalRevenue"

        // Cập nhật biểu đồ
        updateCharts()
    }

    private fun updateCharts() {
        // Cập nhật biểu đồ đơn hàng
        val ordersDataSet = LineDataSet(ordersEntries, "Tổng đơn hàng")
        ordersDataSet.color = ContextCompat.getColor(this, R.color.red) // Đặt màu sắc cho đường biểu đồ
        val lineData = LineData(ordersDataSet)
        binding.ordersLineChart.data = lineData

        // Cập nhật biểu đồ doanh thu
        val revenueDataSet = BarDataSet(revenueEntries, "Giá trị đơn hàng")
        revenueDataSet.color = ContextCompat.getColor(this, R.color.blue) // Đặt màu sắc cho biểu đồ cột
        val barData = BarData(revenueDataSet)
        binding.revenueBarChart.data = barData

        // Cập nhật biểu đồ
        binding.ordersLineChart.invalidate() // Cập nhật biểu đồ đơn hàng
        binding.revenueBarChart.invalidate() // Cập nhật biểu đồ doanh thu
    }
}

