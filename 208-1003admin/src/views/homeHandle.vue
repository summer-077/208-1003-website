<template>
    <el-dialog v-model="visible" title="文件上传">
        <el-form >
            <el-form-item label="">
                <el-upload
                    ref="upload"
                    class="upload-demo"
                    :action="url"
                    :on-preview="handlePreview"
                    :on-remove="handleRemove"
                    :before-remove="beforeRemove"
                    :on-success="successHandle"
                    :headers={token:head}
                    multiple
                    :limit="3"
                    :on-exceed="handleExceed"
                    :file-list="fileList">
                    <el-button size="small" type="primary">点击上传</el-button>
                    <!-- <div slot="tip" class="el-upload__tip">只能上传jpg/png文件，且不超过500kb</div> -->
                </el-upload>
            </el-form-item>
    </el-form>
    </el-dialog>
</template>

<script>
  export default {
    data() {
      return {
        files:{},
        head:localStorage.getItem("token"),
        url:this.$baseUrl+'cos/uploadCosfile?type=file',
        visible:false,
        fileList: [],
        fileName:null,
      };
    },
    methods: {
        init:function(){
            this.visible=true,
            this.head=localStorage.getItem("token")
            console.log("head",this.head);
            this.$nextTick(()=>{
              this.$refs.upload.clearFiles();
            })
            
        },
      handleRemove(file, fileList) {
        debugger
			  let id = file.response.path;
        console.log('path',file);
        
      let that = this;
			let pathes=[]
			pathes.push(id)
			console.log("scope.row",id)
				let data = {
					pathes:pathes
				};
				console.log("scope.row2",data.pathes)
				that.$http('cos/deleteCosFile', 'POST', data, true, function(resp) {
					console.log("cosresp",resp);
					if (resp.code==200) {
						that.$message({
							message: '操作成功',
							type: 'success',
							duration: 1200
            
						});
            that.$emit('refreshDataList',id)
						that.$emit("loadDataList");
					} else {
						that.$message({
							message: '未能删除记录',
							type: 'warning',
							duration: 1200
						});
					}

				});
        that.$emit('refreshDataList',id)
      },
      handlePreview(file) {
       


      },
      handleExceed(files, fileList) {
        this.$message.warning(`当前限制选择 3 个文件，本次选择了 ${files.length} 个文件，共选择了 ${files.length + fileList.length} 个文件`);
      },
      beforeRemove(file, fileList) {
        return this.$confirm(`确定移除 ${ file.name }？`);
      },
      successHandle: function(resp, file, fileList) {
      this.fileName=file.name,
			delete this.files['length']
			let url = file.response.url;
			let path=file.response.path;
      console.log('file',file.name);
			if (resp && resp.code === 200) {
				this.files[url]=path,
        this.$emit('refreshDataList',path,this.fileName)
			} else {
				this.$message.error('文件上传失败');
			}
			
		},
    }
  }
</script>


