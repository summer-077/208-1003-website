<template>
	<el-dialog title="执行归档" width="500px" :close-on-click-modal="false" v-model="visible" :show-close="false">
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
		<template #footer>
			<span class="dialog-footer">
				<el-button size="medium" @click="cancel()">取消</el-button>
				<el-button type="primary" @click="archive()" size="medium" :disabled="disableBtn">{{ btn }}</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	data: function() {
		return {
			visible: false,
			url: this.$baseUrl + 'cos/uploadCosFile?type=archive',
			taskId: '',
			picList: {},//这个变量的类型是json，json存储数据分为key和value，url——key，上传到云端文件的路径和path-value
			disableBtn: false,
			btn: '确定'
		};
	},
	methods: {
		init: function(taskId) {
			let that = this;
			that.visible = true;
			that.taskId = taskId;
			that.btn = '执行归档';
			that.disableBtn = false;
			that.$nextTick(() => {
				that.$refs['upload'].clearFiles();
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
			console.log(fileList)
      //响应、file当前图片，fileList是所有上传成功的图片
			if (resp && resp.code === 200) {
				for (let one of fileList) {
					//遍历上传成功，然后保存到
					this.picList[one.response.url] = one.response.path;
					//picList[key]=value
				}
			} else {
				this.$message.error('图片上传失败');
			}
		},
		removeHandle: function(file, fileList) {
			let that = this;
			let url = file.response.url;
			let path = this.picList[url];
			that.$http('cos/deleteCosFile', 'POST', { pathes: [path] }, true, function(resp) {
				delete that.picList[url];
			});
		},
		cancel: function() {
			let that = this;
			if (Object.keys(that.picList).length > 0) {
				let pathes = Object.values(that.picList);
				that.$http('cos/deleteCosFile', 'POST', { pathes: pathes }, true, function(resp) {
					that.picList = {};
				});
			}
			that.visible = false;
			that.$refs['upload'].clearFiles();
		},
		archive:function(){
			let that=this
			// that.btn="正在归档"
			// that.disableBtn=true
			// if(Object.keys(that.picList).length == 0){
			// 	that.$message({
			// 		message: '没有上传归档照片',
			// 		type: 'error',
			// 		duration: 1200,
			// 	});
			// 	return;
			// }
			//json数组变量
			// let files=[]
			// for(let key in that.picList){//in取出json数组里面的key
			// 	files.push({
			// 		url:key,
			// 		path:that.picList[key]
			// 	})
			// }
			let data={
				taskId:that.taskId,
				// files:JSON.stringify(files)//json数组转化为字符串
			}
			that.$http("approval/archiveTask","POST",data,true,function(resp){
				that.$message({
					message: '操作成功',
					type: 'success',
					duration: 1200,
				});
				that.visible=false
				that.$emit('refreshDataList');
			})
		}
	}
};
</script>

<style lang="less"></style>
