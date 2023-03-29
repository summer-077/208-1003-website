<template>
	<el-dialog title="请假单据" width="500px" :close-on-click-modal="false" v-model="visible" :show-close="false">
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
				<el-button type="primary" @click="archive()" size="medium" >{{ btn }}</el-button>
			</span>
		</template>
	</el-dialog>
</template>

<script>
export default {
	props:{
		list:{
			type:Array,//数组
		}
	},
	data: function() {
		return {
			visible: false,
			url: this.$baseUrl + 'cos/uploadCosFile?type=archive',
			taskId: '',
			picList: {},
			disableBtn: false,
			btn: '确定',
      
		}
    
	},
	methods: {
		init: function(taskId) {
			let that = this;
			that.visible = true;
			data.taskId = that.taskId;
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
			// console.log(fileList)
			if (resp && resp.code === 200) {
				for (let one of fileList) {
					this.picList[one.response.url] = one.response.path;
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
		
	}
};
</script>

<style lang="less"></style>
