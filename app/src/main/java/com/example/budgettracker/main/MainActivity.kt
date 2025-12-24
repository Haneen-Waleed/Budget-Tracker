package com.example.budgettracker.main

import android.os.Bundle
import android.view.View
import android.view.animation.Animation
import android.view.animation.AnimationUtils
import androidx.appcompat.app.AppCompatActivity
import androidx.fragment.app.Fragment
import com.example.budgettracker.fragment.MoneyOperationFragment
import com.example.budgettracker.R
import com.example.budgettracker.databinding.ActivityMainBinding
import com.example.budgettracker.fragment.AddPriorityFragment
import com.example.budgettracker.fragment.HomeFragment
import com.example.budgettracker.fragment.priorities
import com.example.budgettracker.fragment.settings

class MainActivity : AppCompatActivity() {
    // ViewBinding عشان أربط الـ XML بالـ Activity بسهولة وبسرعة
    private lateinit var binding: ActivityMainBinding
    // ده سويتش بسيط بيحدد لو القائمة مفتوحة ولا مقفولة
    // true = مفتوحة، false = مقفولة
    private var clicked = false
    // --- تحميل الأنيميشن مرة واحدة فقط (Lazy) ---
    // أنيميشن فتح الزر الأساسي
    private val rotateOpen: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_open_anim)
    }
    // غلق الزر الأساسي
    private val rotateClose: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.rotate_close_anime)
    }
    // ظهور الزر من تحت
    private val fromBottom: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.from_bottom_anime)
    }

    // اختفاء الزر لنقطة تحت
    private val toBottom: Animation by lazy {
        AnimationUtils.loadAnimation(this, R.anim.to_bottom_anime)
    }

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)

        // تجهيز الـ Binding وربط الواجهة بالـ Activity
        binding = ActivityMainBinding.inflate(layoutInflater)
        setContentView(binding.root)

        // نضمن إن الـ FABs يكونوا قدام بعد ما الواجهة تخلص رسم
        binding.root.post { bringFabsToFront() }

        // زرار القائمة الرئيسي
        binding.fabMain.setOnClickListener { toggleMenu() }

        // فتح "Add Priority" Fragment لما يضغط على زرار "priorities"
        binding.fabPriorities.setOnClickListener {
            val frag = AddPriorityFragment()
            openFragment(frag)
        }

        // فتح إضافة فلوس
        binding.fabAddMoney.setOnClickListener {
            val frag = MoneyOperationFragment().apply {
                arguments = Bundle().apply { putBoolean("isAdd", true) }
            }
            openFragment(frag)
        }

        // فتح سحب فلوس
        binding.fabWithdraw.setOnClickListener {
            val frag = MoneyOperationFragment().apply {
                arguments = Bundle().apply { putBoolean("isAdd", false) }
            }
            openFragment(frag)
        }

        // السفلي Bottom navigation
        binding.bottomNavigationView.setOnItemSelectedListener {
            when (it.itemId) {
                R.id.home -> openFragment(HomeFragment())
                R.id.priorities -> openFragment(priorities())
                R.id.settings -> openFragment(settings())
            }
            true
        }

        // أول ما التطبيق يفتح افتح الصفحة الرئيسية
        openFragment(HomeFragment())
    }


    // --------------------------------------------------------
    // دالة فتح وغلق القائمة (Toggle Menu)
    // --------------------------------------------------------
    private fun toggleMenu() {
        setAnimation(clicked)
        setClickable(clicked)
        clicked = !clicked
    }


    // --------------------------------------------------------
    // التحكم في الأنيميشن حسب حالة القائمة
    // --------------------------------------------------------
    private fun setAnimation(isMenuOpen: Boolean) {

        // لو القائمة مقفولة → افتحها
        if (!isMenuOpen) {
            setVisibility(false)
            binding.fabMain.startAnimation(rotateOpen)
            binding.fabAddMoney.startAnimation(fromBottom)
            binding.fabWithdraw.startAnimation(fromBottom)
            binding.fabPriorities.startAnimation(fromBottom)

        } else {
            // لو مفتوحة → اقفلها
            binding.fabMain.startAnimation(rotateClose)
            binding.fabAddMoney.startAnimation(toBottom)
            binding.fabWithdraw.startAnimation(toBottom)
            binding.fabPriorities.startAnimation(toBottom)

            // بعد ما الأنيميشن يخلص ارجع اخفيهم
            toBottom.setAnimationListener(object : Animation.AnimationListener {
                override fun onAnimationStart(animation: Animation?) {}
                override fun onAnimationRepeat(animation: Animation?) {}

                override fun onAnimationEnd(animation: Animation?) {
                    setVisibility(true)
                    setClickable(false)
                    toBottom.setAnimationListener(null)
                }
            })
        }
    }


    // --------------------------------------------------------
    // التحكم في ظهور وإخفاء الـ FABs
    // true  → يخفي
    // false → يظهر
    // --------------------------------------------------------
    private fun setVisibility(isClosed: Boolean) {
        val visibility = if (isClosed) View.INVISIBLE else View.VISIBLE
        binding.fabAddMoney.visibility = visibility
        binding.fabWithdraw.visibility = visibility
        binding.fabPriorities.visibility = visibility
    }


    // --------------------------------------------------------
    // التحكم في قابلية الضغط
    // --------------------------------------------------------
    private fun setClickable(isMenuOpen: Boolean) {
        val clickable = !isMenuOpen
        binding.fabAddMoney.isClickable = clickable
        binding.fabWithdraw.isClickable = clickable
        binding.fabPriorities.isClickable = clickable
    }


    // --------------------------------------------------------
    // دالة لتحديد هل الفراجمنت الحالي مسموح يظهر فيه الـ FAB
    // هنا أخفينا الـ FABs في priorities فقط
    // --------------------------------------------------------
    private fun shouldShowFab(fragment: Fragment): Boolean {
        return fragment is HomeFragment
    }



    // --------------------------------------------------------
    // دالة فتح فراجمنت بالطريقة الصحيحة
    // هنا بقى حلينا مشكلة إن الـ FAB يرجع يظهر غصب عنك
    // --------------------------------------------------------
    private fun openFragment(fragment: Fragment) {

        // لو القائمة كانت مفتوحة → اقفلها صح
        if (clicked) {
            clicked = false
            binding.fabMain.startAnimation(rotateClose)
            setVisibility(true)
            setClickable(false)
        }

        // افتح الفراجمنت
        val transaction = supportFragmentManager.beginTransaction()
            .replace(R.id.frame_layout, fragment)

        if (fragment !is HomeFragment) {
            transaction.addToBackStack(null)
        }

        transaction.commit()


        binding.root.post {
            if (fragment is HomeFragment) {
                showMainFab()
                bringFabsToFront()
            } else {
                hideMainFab()
            }
        }


    }



    // --------------------------------------------------------
    // وضع الـ FABs في الأمام على الواجهة
    // --------------------------------------------------------
    private fun bringFabsToFront() {
        binding.fabMain.bringToFront()
        binding.fabAddMoney.bringToFront()
        binding.fabWithdraw.bringToFront()
        binding.fabPriorities.bringToFront()
    }


    // --------------------------------------------------------
    // دوال عامة يقدر أي Fragment يستدعيها
    // --------------------------------------------------------

    fun showMainFab() {

        // اقفل أي أنيميشن شغاله
        binding.fabMain.clearAnimation()
        binding.fabAddMoney.clearAnimation()
        binding.fabWithdraw.clearAnimation()
        binding.fabPriorities.clearAnimation()

        // أظهر الزرار الرئيسي فقط
        binding.fabMain.visibility = View.VISIBLE

        // اقفل القائمة تماماً
        binding.fabAddMoney.visibility = View.INVISIBLE
        binding.fabWithdraw.visibility = View.INVISIBLE
        binding.fabPriorities.visibility = View.INVISIBLE

        // تأكد إنهم مش قابلين للضغط
        binding.fabAddMoney.isClickable = false
        binding.fabWithdraw.isClickable = false
        binding.fabPriorities.isClickable = false

        // نضمن إن حالة القائمة مقفولة
        clicked = false
    }



    fun hideMainFab() {
        binding.fabMain.clearAnimation()
        binding.fabAddMoney.clearAnimation()
        binding.fabWithdraw.clearAnimation()
        binding.fabPriorities.clearAnimation()

        binding.fabMain.visibility = View.GONE
        binding.fabWithdraw.visibility = View.GONE
        binding.fabPriorities.visibility = View.GONE
        binding.fabAddMoney.visibility = View.GONE
    }


}