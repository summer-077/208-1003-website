<template>
	<div>
	<el-dialog title="驳回理由" :close-on-click-modal="false" v-model="visible" width="692px">
		<el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="60px">
			<el-form-item label="理由" prop="reject" >
				<!-- 标签是主题，内容保存在title中 -->
				<el-input
				 v-model="dataForm.reject" 
				  placeholder="请输入驳回理由"
				 size="medium" 
				 style="width:100%" 
				 clearable="clearable" />
			</el-form-item>
		</el-form>
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="visible = false">取消</el-button>
				<el-button type="primary" size="medium" @click="dataFormSubmit">确定</el-button>
			</span>
		</template>
		
	</el-dialog>

			
	</div>
</template>

<script>
import pic from "./leave-add-pic.vue"
export default {
	components:{
		pic,
	},
	//监听变化
	watch:{
      
	},
	data: function() {
		return {
			//控制弹窗是否显示
			id:"",
			variety:"",
			taskId:"",
		  picVisible:false,
			visible: false,
			dataForm: {
			   reject:null,

			},
			dataRule: {
		  	reject: [{ required: true, message: '请假原因不能为空' }],
			}
		};
	},

	methods: {
		//init函数是用来处理弹窗的
		init: function(taskId) {
			let that = this;
			that.visible = true;
			that.$nextTick(() => {
				that.$refs['dataForm'].resetFields();
			// 	that.$http('userarchAllUser', 'GET', null, true, function(resp) {
			// 		let temp = [];
			// 		for (let one of resp.list) {
			// 			temp.push({ key: one.id, label: one.name });
			// 		}
			// 		that.users = temp;
			// 	});
			 });
		},
		// loadPlaceList: function() {
		// 	let that = this;
		// 	that.dataForm.place = null;
		// 	that.placeList = [];
		// 	if (
		// 		that.dataForm.date == null ||
		// 		that.dataForm.date == '' ||
		// 		that.dataForm.start == null ||
		// 		that.dataForm.start == '' ||
		// 		that.dataForm.end == null ||
		// 		that.dataForm.end == ''
		// 	) {
		// 		return;
		// 	}
		// 	let data = {
		// 		date: dayjs(that.dataForm.date).format('YYYY-MM-DD'),
		// 		start: that.dataForm.start,
		// 		end: that.dataForm.end
		// 	};
		// 	that.$http('meeting_roomarchFreeMeetingRoom', 'POST', data, true, function(resp) {
		// 		that.placeList = resp.list;
		// 	});
		// },
    //提交驳回理由的位置
		dataFormSubmit:function(){
			let that=this
			let data=that.dataForm
			data.instanceId=this.id
			data.taskId=this.taskId,
		  data.variety=this.variety,
			data.approval='不同意',
			console.log("reject",that.dataForm.reject);
			// this.$refs['dataForm'].validate(valid => {
			// 	if(valid){
					that.$http("approval/approvalTask","POST",data,true,function(resp){
					   that.$parent.loadDataList();
						// if(resp.rows==1){
							//将弹窗页面隐藏起来
							that.visible=false
							that.$message({
							    message: '操作成功',
							    type: 'success',
							    duration: 1200
							});
							
							setTimeout(function(){
								that.$emit('refresh'); //加载最新数据
							},1200)
						// }
						// else{
						// 	that.$message({
						// 		message: '操作失败',
						// 		type: 'error',
						// 		duration: 1200
						// 	});
						// }
					})
			// 	}
			// })
		}
	},
};
</script>

<style lang="less" scoped="scoped">
</style>