package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.forms

import android.app.Activity
import android.content.Intent
import android.os.Bundle
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.*
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.cardview.widget.CardView
import androidx.constraintlayout.widget.ConstraintLayout
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.BaseActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.MainActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.LeaveDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.CameraService
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.AddPlantTasksAdapter
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.NewPlantInstance
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DBService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.DateTimeService
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.CloudinaryService
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.ImageLoadingService
import com.mobdeve.s15.group8.mobdeve_mp.model.services.PlantService
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.singletons.LeaveDialogType
import java.util.*

class AddPlantActivity : BaseActivity(), AddPlantTasksAdapter.OnTaskDeletedListener {
    private lateinit var tasksRV: RecyclerView
    private lateinit var cvNoTasks: CardView
    private lateinit var ivPlant: ImageView
    private lateinit var ivAddPlant: ImageView
    private lateinit var ibtnAddTask: ImageButton
    private lateinit var ibtnSavePlant: ImageButton
    private lateinit var tvErrNickname: TextView
    private lateinit var tvErrName: TextView
    private lateinit var tvErrImage: TextView
    private lateinit var etPlantName: EditText
    private lateinit var etPlantNickname: EditText
    private lateinit var groupNoPic: ConstraintLayout
    private var mPhotoFilename = ""
    private var mFirstTime = true
    private val mPlantId = UUID.randomUUID().toString()

    private val addTaskLauncher =
        registerForActivityResult(StartActivityForResult()) { result ->
            if (result.resultCode == Activity.RESULT_OK) {
                val action = result.data?.getCharSequenceExtra(
                    getString(R.string.ADD_TASK_ACTION)).toString()
                val startDate = result.data?.getLongExtra(
                    getString(R.string.ADD_TASK_START_DATE), 0)
                val occurrence = result.data?.getCharSequenceExtra(
                    getString(R.string.ADD_TASK_OCCURRENCE)).toString()
                val repeat = result.data?.getIntExtra(
                    getString(R.string.ADD_TASK_REPEAT), 0) as Int
                val weeklyRecurrence = result.data?.getIntegerArrayListExtra(getString(R.string.ADD_TASK_WEEKLY_RECURRENCE))

                val lastCompleted = DateTimeService.getLastDueDate(
                    occurrence,
                    repeat,
                    Date(startDate!!),
                    weeklyRecurrence
                )

                Log.d("RECURRENCE", "last completed: ${lastCompleted}")

                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    plantId = mPlantId,
                    userId = F.auth.uid!!,
                    action = action,
                    startDate = Date(startDate),
                    occurrence = occurrence,
                    repeat = repeat,
                    lastCompleted = lastCompleted.time,
                    weeklyRecurrence = weeklyRecurrence!!
                )
                NewPlantInstance.addTask(newTask)
                (tasksRV.adapter as AddPlantTasksAdapter).addNewTask(newTask)

                mShowOrHideNoTasksCard()
                mShowOrHideAddTaskBtn()
            }
        }

    private val cameraLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) { // fetch bitmap and show image to user
            groupNoPic.visibility = View.GONE
            ivPlant.visibility = View.VISIBLE
            ImageLoadingService.loadImage(
                filePath=mPhotoFilename,
                context=this,
                imageView=ivPlant)
        }
    }
    override val layoutResourceId: Int = R.layout.activity_add_plant
    override val mainViewId: Int = R.id.layout_add_plant

    override fun onCreate(savedInstanceState: Bundle?) {
        NewPlantInstance.resetPlant()
        NewPlantInstance.resetTasks()
        super.onCreate(savedInstanceState)
    }

    override fun onStart() {
        super.onStart()
        mShowOrHideAddTaskBtn()
    }

    override fun inititalizeViews() {
        groupNoPic = findViewById(R.id.group_no_pic)
        ivAddPlant = findViewById(R.id.iv_no_pic)
        ivPlant = findViewById(R.id.iv_add_plant)
        etPlantName = findViewById(R.id.et_plant_name)
        etPlantNickname = findViewById(R.id.et_plant_nickname)
        tvErrName = findViewById(R.id.tv_err_name)
        tvErrNickname = findViewById(R.id.tv_edit_err_nickname)
        tvErrImage = findViewById(R.id.tv_err_image)
        ibtnAddTask = findViewById(R.id.ibtn_add_task)
        ibtnSavePlant = findViewById(R.id.ibtn_save_plant)
        tasksRV = findViewById(R.id.rv_tasks)
        cvNoTasks = findViewById(R.id.cv_no_tasks)
        tasksRV.adapter = AddPlantTasksAdapter(this, NewPlantInstance.tasksObject)
        tasksRV.layoutManager = LinearLayoutManager(this)
    }

    override fun bindData() {
        mShowOrHideNoTasksCard()
    }

    override fun bindActions() {
        CloudinaryService.setOnUploadSuccessListener { imageUrl ->
            DBService.updateDocument(
                collection= F.plantsCollection,
                id=mPlantId,
                field="imageUrl",
                value=imageUrl)
        }

        etPlantName.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                if (!mFirstTime) {
                    tvErrName.visibility = if (s!!.isNotEmpty()) View.GONE else View.VISIBLE
                }
            }
        })

        etPlantNickname.addTextChangedListener(object: TextWatcher {
            override fun afterTextChanged(s: Editable?) {}
            override fun beforeTextChanged(s: CharSequence?, start: Int, count: Int, after: Int) {}
            override fun onTextChanged(s: CharSequence?, start: Int, before: Int, count: Int) {
                tvErrNickname.visibility = if (mCheckNickname()) View.GONE else View.VISIBLE
            }
        })

        ivAddPlant.setOnClickListener { mLaunchCamera() }
        ivPlant.setOnClickListener { mLaunchCamera() }
        ibtnSavePlant.setOnClickListener { mSavePlant() }
        ibtnAddTask.setOnClickListener {
            val i = Intent(this, AddTaskActivity::class.java)
            addTaskLauncher.launch(i)
        }
    }

    override fun notifyTaskDeleted(task: Task) {
        NewPlantInstance.removeTask(task)
        mShowOrHideNoTasksCard()
        mShowOrHideAddTaskBtn()
    }

    override fun onSaveInstanceState(outState: Bundle) {
        super.onSaveInstanceState(outState)
        outState.putSerializable(getString(R.string.SAVED_PLANT_KEY), NewPlantInstance.plant)
    }

    override fun onRestoreInstanceState(savedInstanceState: Bundle) {
        super.onRestoreInstanceState(savedInstanceState)
        val plant = savedInstanceState.getSerializable(getString(R.string.SAVED_PLANT_KEY))
    }

    override fun onBackPressed() {
        if (mCheckFields("leave")) {
            val fragment = LeaveDialogFragment(LeaveDialogType.ADD_PLANT.ordinal)
            fragment.show(supportFragmentManager, "leave add plant")
        } else {
            super.onBackPressed()
        }
    }

    private fun mLaunchCamera() {
        Log.d("CAM", "open cam")
        mPhotoFilename = CameraService.launchCameraAndGetFilename(
            context=this,
            authority=getString(R.string.file_provider_authority),
            launcher=cameraLauncher)
        Log.d("CAM", "Filename: $mPhotoFilename")
    }

    private fun mCheckNickname(): Boolean {
        val currentNickname = etPlantNickname.text.toString().trim()
        val plant = PlantService.findPlantByNickname(currentNickname)
        return plant == null || // for newly-named plants
                currentNickname.isEmpty() || // empty nicknames are not counted as duplicates
                currentNickname != plant.nickname // changed nickname and unique
    }

    private fun mCheckFields(type: String): Boolean {
        mFirstTime = false

        val nameValid = etPlantName.text.trim().isNotEmpty()
        val nicknameValid = if (type == "save") mCheckNickname() else etPlantNickname.text.trim().isNotEmpty()
        val imageValid = groupNoPic.visibility == View.GONE && ivPlant.visibility == View.VISIBLE

        return if (type == "save") {
            tvErrName.visibility = if (!nameValid) View.VISIBLE else View.GONE
            tvErrNickname.visibility = if (!nicknameValid) View.VISIBLE else View.GONE
            tvErrImage.visibility = if (!imageValid) View.VISIBLE else View.GONE

            nameValid && nicknameValid && imageValid
        } else {
            nameValid || nicknameValid || imageValid
        }
    }

    private fun mSavePlant() {
        if (!mCheckFields("save")) {
            return
        }

        // Compile final map to write to firebase
        NewPlantInstance.setStaticParams(
            id=mPlantId,
            userId=F.auth.uid!!,
            name=etPlantName.text.toString().trim(),
            nick=etPlantNickname.text.toString().trim(),
            filePath=mPhotoFilename,
            death=false)

        // Write plant to firebase first
        DBService.addDocument(
            collection=F.plantsCollection,
            id=mPlantId,
            data=NewPlantInstance.plant)

        // Write the tasks to firebase
        for (task in NewPlantInstance.tasks) {
            // update the plantId first
            task["plantId"] = mPlantId
            DBService.addDocument(
                collection = F.tasksCollection,
                id = task["id"].toString(),
                data = task
            )
        }

        // Write to local
        for (taskObj in NewPlantInstance.tasksObject) {
            taskObj.plantId = mPlantId
            PlantRepository.taskList.add(taskObj)
        }

        // Notify the ViewAllPlantsAdapter of a dataset change
        PlantRepository.plantList.add(NewPlantInstance.plantObject)
        NewPlantInstance.notifyPlantRV()

        // Then upload to cloudinary
        try {
            CloudinaryService.uploadToCloud(mPhotoFilename, mPlantId)
        } catch (err: Error) {
            MediaManager.init(this)
        }

        // Reset the new plant instance
        NewPlantInstance.resetPlant()
        NewPlantInstance.resetTasks()

        // Go back to MainActivity
        Intent(this@AddPlantActivity, MainActivity::class.java)
        setResult(Activity.RESULT_OK)
        finish()
    }

    private fun mShowOrHideNoTasksCard() {
        if (NewPlantInstance.tasks.size == 0)
            cvNoTasks.visibility = View.VISIBLE
        else
            cvNoTasks.visibility = View.GONE
    }

    private fun mShowOrHideAddTaskBtn() {
        if (NewPlantInstance.tasks.size
            >= resources.getStringArray(R.array.actions_array).size) {
            ibtnAddTask.visibility = View.GONE
        } else {
            ibtnAddTask.visibility = View.VISIBLE
        }
    }
}