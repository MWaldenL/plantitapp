package com.mobdeve.s15.group8.mobdeve_mp.controller.activities.forms

import android.app.Activity
import android.content.Intent
import android.text.Editable
import android.text.TextWatcher
import android.util.Log
import android.view.View
import android.widget.EditText
import android.widget.ImageButton
import android.widget.ImageView
import android.widget.TextView
import androidx.activity.result.contract.ActivityResultContracts.StartActivityForResult
import androidx.cardview.widget.CardView
import androidx.recyclerview.widget.LinearLayoutManager
import androidx.recyclerview.widget.RecyclerView
import com.cloudinary.android.MediaManager
import com.google.firebase.Timestamp
import com.mobdeve.s15.group8.mobdeve_mp.R
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.BaseActivity
import com.mobdeve.s15.group8.mobdeve_mp.controller.activities.fragments.dialogs.LeaveDialogFragment
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.CameraService
import com.mobdeve.s15.group8.mobdeve_mp.controller.adapters.AddPlantTasksAdapter
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.CloudinaryService
import com.mobdeve.s15.group8.mobdeve_mp.controller.services.ImageLoadingService
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Plant
import com.mobdeve.s15.group8.mobdeve_mp.model.dataobjects.Task
import com.mobdeve.s15.group8.mobdeve_mp.model.repositories.PlantRepository
import com.mobdeve.s15.group8.mobdeve_mp.model.services.*
import com.mobdeve.s15.group8.mobdeve_mp.singletons.F
import com.mobdeve.s15.group8.mobdeve_mp.singletons.LeaveDialogType
import java.util.*

class EditPlantActivity : BaseActivity(), AddPlantTasksAdapter.OnTaskDeletedListener {
    private lateinit var rvTasks: RecyclerView
    private lateinit var cvNoTasks: CardView
    private lateinit var ivPlant: ImageView
    private lateinit var ibtnAddTask: ImageButton
    private lateinit var ibtnSave: ImageButton
    private lateinit var etPlantName: EditText
    private lateinit var etPlantNickname: EditText
    private lateinit var tvErrName: TextView
    private lateinit var tvErrNickname: TextView
    private lateinit var mPlantData: Plant
    private lateinit var mPlantDataEditable: Plant
    private var mPhotoFilename = ""
    private var mTempPhotoFilename = ""
    private var mOldNickname = "NULL"
    private var mFirstTime = true
    private val mNewTasks: ArrayList<Task> = arrayListOf()
    private val mDeletedTasks: ArrayList<String> = arrayListOf()

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

                val newTask = Task(
                    id = UUID.randomUUID().toString(),
                    plantId = mPlantData.id,
                    userId = F.auth.uid!!,
                    action = action,
                    startDate = Date(startDate!!),
                    occurrence = occurrence,
                    repeat = repeat,
                    lastCompleted = DateTimeService.getLastDueDate(
                        occurrence,
                        repeat,
                        Date(startDate),
                        weeklyRecurrence
                    ).time,
                    weeklyRecurrence = weeklyRecurrence!!
                )
                mNewTasks.add(newTask)
                mPlantDataEditable.tasks.add(newTask.id)
                (rvTasks.adapter as AddPlantTasksAdapter).addNewTask(newTask)

                mShowOrHideNoTasksCard()
                mShowOrHideAddTaskBtn()
            }
        }

    private val cameraLauncher = registerForActivityResult(StartActivityForResult()) { result ->
        if (result.resultCode == RESULT_OK) {
            mPhotoFilename = mTempPhotoFilename
//            ImageLoadingService.loadImageLocal(mPhotoFilename, this, ivPlant)
            ImageLoadingService.loadImage(mPhotoFilename, mPlantData.imageUrl, this, ivPlant)
        }
    }

    override val layoutResourceId: Int = R.layout.activity_edit_plant
    override val mainViewId: Int = R.id.layout_edit

    override fun onStart() {
        super.onStart()
        mShowOrHideAddTaskBtn()
    }

    override fun inititalizeViews() {
        ivPlant = findViewById(R.id.iv_plant_edit)
        ibtnAddTask = findViewById(R.id.ibtn_add_task_edit)
        ibtnSave = findViewById(R.id.ibtn_save_plant_edit)
        etPlantName = findViewById(R.id.et_plant_name_edit)
        etPlantNickname = findViewById(R.id.et_plant_nickname_edit)
        tvErrName = findViewById(R.id.tv_edit_err_name)
        tvErrNickname = findViewById(R.id.tv_edit_err_nickname)
        cvNoTasks = findViewById(R.id.cv_no_tasks_edit)
        rvTasks = findViewById(R.id.rv_tasks_edit)
        rvTasks.layoutManager = LinearLayoutManager(this)
    }

    override fun bindData() {
        mPlantData = intent.getParcelableExtra(getString(R.string.PLANT_KEY))!!
        mPlantDataEditable = mPlantData.deepCopy()

        val (id, userId, imageUrl, filePath, name, nickname, datePurchased, death, taskIds, journal) = mPlantData
        val tasks = TaskService.findTasksByPlantId(id)

        mPhotoFilename = filePath
        ImageLoadingService.loadImage(filePath, imageUrl, this, ivPlant)

        etPlantName.setText(name)
        etPlantNickname.setText(nickname)
        mOldNickname = etPlantNickname.text.toString()
        mOldNickname.trim()

        rvTasks.adapter = AddPlantTasksAdapter(this, tasks)

        mShowOrHideNoTasksCard()
    }

    override fun bindActions() {
        CloudinaryService.setOnUploadSuccessListener { imageUrl ->
            DBService.updateDocument(
                F.plantsCollection,
                mPlantData.id,
                "imageUrl",
                imageUrl)
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
                if (mOldNickname != "NULL") {
                    tvErrNickname.visibility = if (mCheckNickname()) View.GONE else View.VISIBLE
                }
            }
        })
        ivPlant.setOnClickListener { mLaunchCamera() }
        ibtnSave.setOnClickListener { mSavePlant() }
        ibtnAddTask.setOnClickListener {
            val i = Intent(this, AddTaskActivity::class.java)
            i.putExtra(getString(R.string.EDIT_PLANT_TASKS), mNewTasks)
            i.putExtra(getString(R.string.EDIT_PLANT_OLD_TASKS), mPlantDataEditable.tasks)
            addTaskLauncher.launch(i)
        }
    }

    private fun mLaunchCamera() {
        mTempPhotoFilename = CameraService.launchCameraAndGetFilename(
            context=this,
            authority=getString(R.string.file_provider_authority),
            launcher=cameraLauncher)
    }

    override fun onBackPressed() {
        if (mCheckChanges()) {
            val fragment = LeaveDialogFragment(LeaveDialogType.EDIT_PLANT.ordinal)
            fragment.show(supportFragmentManager, "leave edit plant")
        } else {
            super.onBackPressed()
        }
    }

    private fun mCheckNickname(): Boolean {
        val currentNickname = etPlantNickname.text.toString().trim()
        val plant = PlantService.findPlantByNickname(currentNickname)
        return plant == null || // for newly-named plants
                currentNickname.isEmpty() || // empty nicknames are not counted as duplicates
                currentNickname == mOldNickname || // if did not change nickname
                currentNickname != plant.nickname // changed nickname and unique
    }

    private fun mCheckChanges(): Boolean {
        val nameChanged = mPlantData.name != etPlantName.text.toString()
        val nicknameChanged = mPlantData.nickname != etPlantNickname.text.toString()
        val tasksChanged = !mPlantData.tasks.equals(mPlantDataEditable.tasks)
        val photoChanged = mPlantData.filePath != mPhotoFilename

        return nameChanged || nicknameChanged || tasksChanged || photoChanged
    }

    private fun mShowOrHideNoTasksCard() {
        if (mPlantDataEditable.tasks.size == 0)
            cvNoTasks.visibility = View.VISIBLE
        else
            cvNoTasks.visibility = View.GONE
    }

    private fun mShowOrHideAddTaskBtn() {
        if (rvTasks.adapter!!.itemCount >= resources.getStringArray(R.array.actions_array).size) {
            ibtnAddTask.visibility = View.GONE
        } else {
            ibtnAddTask.visibility = View.VISIBLE
        }
    }

    private fun mCheckFields(): Boolean {
        mFirstTime = false
        val nameFilled = etPlantName.text.isNotEmpty()
        val nicknameUnique = mCheckNickname()
        tvErrName.visibility = if (!nameFilled) View.VISIBLE else View.GONE
        tvErrNickname.visibility = if (!nicknameUnique) View.VISIBLE else View.GONE
        return nameFilled && nicknameUnique
    }

    private fun mSavePlant() {
        if (!mCheckFields()) {
            return
        }

        mPlantDataEditable.name = etPlantName.text.toString().trim()
        mPlantDataEditable.nickname = etPlantNickname.text.toString().trim()
        if (mPlantData.filePath !== mPhotoFilename) {
            Log.d("MPEditPlant", "changed filename")
            mPlantDataEditable.filePath = mPhotoFilename
        }
//        mPlantDataEditable.imageUrl = ""

        // Update the changed plant fields in firebase
        DBService.updateDocument(
            F.plantsCollection,
            mPlantData.id,
            hashMapOf(
                "name" to mPlantDataEditable.name,
                "nickname" to mPlantDataEditable.nickname,
                "tasks" to mPlantDataEditable.tasks,
                "filePath" to mPhotoFilename
            )
        )

        // Update the tasks in firebase
        for (task in mNewTasks) {
            DBService.addDocument(
                F.tasksCollection,
                task.id,
                hashMapOf(
                    "id" to task.id,
                    "plantId" to task.plantId,
                    "userId" to task.userId,
                    "action" to task.action,
                    "startDate" to Timestamp(task.startDate),
                    "repeat" to task.repeat,
                    "occurrence" to task.occurrence,
                    "lastCompleted" to Timestamp(task.lastCompleted),
                    "weeklyRecurrence" to task.weeklyRecurrence
                )
            )
            PlantRepository.taskList.add(task)
        }

        // Delete tasks from firebase and locally
        for (taskID in mDeletedTasks) {
            DBService.deleteDocument(
                F.tasksCollection,
                taskID
            )
            val task = TaskService.findTaskById(taskID)
            PlantRepository.taskList.remove(task)
        }

        // Upload image to cloud only if it has been changed
        if (mPlantData.filePath != mPhotoFilename) {
            try {
                CloudinaryService.deleteFromCloud(mPlantData.imageUrl)
                CloudinaryService.uploadToCloud(mPhotoFilename, mPlantData.id)
            } catch (err: Error) {
                MediaManager.init(this)
            }
        }

        // Prepare to send updated data back to plant view
        val index = PlantRepository.plantList.indexOf(mPlantData)
        PlantRepository.plantList[index] = mPlantDataEditable

        mPlantData = PlantRepository.plantList[index]
        val resultIntent = Intent()
        resultIntent.putExtra(getString(R.string.PLANT_KEY), mPlantData)
        setResult(Activity.RESULT_OK, resultIntent)
        finish()
    }

    override fun notifyTaskDeleted(task: Task) {
        mDeletedTasks.add(task.id)
        mPlantDataEditable.tasks.remove(task.id)
        mNewTasks.remove(task)
        mShowOrHideNoTasksCard()
        mShowOrHideAddTaskBtn()
    }
}