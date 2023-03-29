<template>
<div>
	<el-dialog ref="add" title="请假申请" :close-on-click-modal="false" v-model="visible" width="450px">
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
			<el-form-item label="请假原因" prop="reason">
				<el-input
					type="textarea"
					v-model="dataForm.reason"
					placeholder="请假原因"
					:autosize="{ minRows: 4, maxRows: 6 }"
					clearable="clearable"
				/>
			</el-form-item>
			<el-form-item label="开始日期">
				<el-row type="flex" justify="space-between">
					<el-col :span="12">
						<el-form-item prop="startDate" class="form-item">
							<el-date-picker
								v-model="dataForm.startDate"
								value-format="yyyy-MM-dd"
								type="date"
								placeholder="开始日期"
								style="width: 100%;"
								:disabledDate="disabledDate"
								clearable="clearable"
							></el-date-picker>
						</el-form-item>
					</el-col>
					<el-col :span="11">
						<el-form-item prop="startTime" class="form-item">
							<el-time-select
								v-model="dataForm.startTime"
								start='08:30'
								step='00:30'
								end='20:30'
								value-format="HH:mm"
								placeholder="选择时间"
								style="width: 100%;"
							></el-time-select>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form-item>
			<el-form-item label="截止日期">
				<el-row type="flex" justify="space-between">
					<el-col :span="12">
						<el-form-item prop="endDate" class="form-item">
							<el-date-picker
								v-model="dataForm.endDate"
								value-format="yyyy-MM-dd"
								type="date"
								placeholder="截止日期"
								style="width: 100%;"
								:disabledDate="disabledDate"
								clearable="clearable"
							></el-date-picker>
						</el-form-item>
					</el-col>
					<el-col :span="11">
						<el-form-item prop="endTime" class="form-item">
							<el-time-select
								v-model="dataForm.endTime"
								value-format="HH:mm"
								start='08:30'
								step='00:30'
								end='20:30'
								placeholder="选择时间"
								style="width: 100%;"
							></el-time-select>
						</el-form-item>
					</el-col>
				</el-row>
			</el-form-item>
			<el-form-item label="请假类型" prop="type">
				<el-radio-group v-model="dataForm.type">
					<el-radio :label="1">病假</el-radio>
					<el-radio :label="2">事假</el-radio>
				</el-radio-group>
			</el-form-item >
			<el-form-item label="请假单据">
		<el-upload
			ref="upload"
			:action="url"
			list-type="picture-card"
			accept=".jpg,.jpeg,.png"
			with-credentials="true"
			:before-upload="beforeUploadHandle"
			:on-success="successHandle"
			:on-remove="removeHandle"
		>
			<i class="el-icon-plus"></i>
		</el-upload>
		</el-form-item>
		
		</el-form>
		
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">取消</el-button>
				<el-button type="primary" size="medium" @click="dataFormSubmit">确定</el-button>
			</span>
		</template>
	</el-dialog>
	<!-- <pic ref="pic" @refreshDataList="loadDataList"></pic> -->
	</div>
</template>

<script>
import pic from "./leave-add-pic.vue"
import dayjs from 'dayjs';

export default {
	component:{
	pic,
},
	data: function() {
		return {
			url: this.$baseUrl + 'cos/uploadCosFile?type=leaveinsert',
			picList: {},
			visible: false,
			reVisible:true,
			files:{},
			dataForm: {
				reason: null,
				startDate: null,
				startTime: null,
				endDate: null,
				endTime: null,
				type: null,
				
			},
			disabledDate(date) {
				return dayjs(date.toLocaleDateString()).isBefore(new Date().toLocaleDateString());
			},
			dataRule: {
				reason: [{ required: true, message: '请假原因不能为空' }],
				startDate: [{ required: true, message: '开始日期不能为空' }],
				startTime: [{ required: true, message: '开始时间不能为空' }],
				endDate: [{ required: true, message: '截止日期不能为空' }],
				endTime: [{ required: true, message: '截止时间不能为空' }],
				type: [{ required: true, message: '请假类型不能为空' }],
				upload:[{required:true,message:'单据不能为空'}]
			}
		};
	},
	methods: {
				init: function(taskId) {
			let that = this;
			that.visible = true;
			that.visible = true;
			that.taskId = taskId;
			that.btn = '执行归档';
			that.disableBtn = false;
			that.$nextTick(() => {
				that.$refs['dataForm'].resetFields();
				that.$refs['upload'].clearFiles();
				if(that.files)
				{
                    that.files.length=0;
				}
				
			});
		},
        
		dataFormSubmit:function(file){
			debugger
			let that=this
			let arr=[]
			console.log("arr.length1",arr.length)
			for(let key in that.files){//in取出json数组里面的key
				arr.push({
					url:key,
					path:that.files[key]
				})
			}
			
			let data={
				//请假理由
				reason:that.dataForm.reason,
				start: dayjs(that.dataForm.startDate).format('YYYY-MM-DD') + ' ' + that.dataForm.startTime + ':00',
				end: dayjs(that.dataForm.endDate).format('YYYY-MM-DD') + ' ' + that.dataForm.endTime + ':00',
				type:that.dataForm.type,
				files:JSON.stringify(arr),
			}
			if(arr.length== 0){
				that.$message({
					message: '没有上传归档照片',
					type: 'error',
					duration: 1200,
				});
				return;
			}
			this.$refs['dataForm'].validate(valid => {
				console.log("valid",valid);
				console.log("所传file",this.files)
				if (valid) {
					that.$http("leave/insert","POST",data,true,function(resp){
						console.log("所传file",that.files)
						console.log("this",this);
						console.log("that",that);
						if(resp.rows==1){
							that.visible=false
							that.$message({
								message: '操作成功',
								type: 'success',
								duration: 1200,
							});
							setTimeout(function(){
								that.$emit('refreshDataList');
								
							},1200)
						}else{
							that.$message({
								message: '操作失败',
								type: 'error',
								duration: 1200,
							});
						}
					})
				}
			})
			that.$nextTick(() => {
				//that.$refs['upload'].clearFiles();
			});
		},

		beforeUploadHandle: function(file) {
			if (file.type !== 'image/jpg' && file.type !== 'image/jpeg' && file.type !== 'image/png') {
				this.$message.error('只支持jpg、png格式的图片！');
				return false;
			}
			return true;
		},
		successHandle: function(resp, file, fileList) {
			debugger
			delete this.files['length']
			let url = file.response.url;
			let path=file.response.path;
			if (resp && resp.code === 200) {
				this.files[url]=path
			} else {
				this.$message.error('图片上传失败');
			}
			
		},
		removeHandle: function(file, fileList) {
			let that = this;
			let url = file.response.url;
			that.$http('cos/deleteCosFile', 'POST', { pathes: [url] }, true, function(resp) {
				delete that.files[url];
			});
		},
		
	},
	
};
</script>

<style lang="less" scoped="scoped">
.form-item {
	margin-bottom: 0 !important;
}
</style>
