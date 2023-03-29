<template>
	<div>
		<el-form>
			<el-form-item>
                <el-button type="danger" size="medium" @click.native="workHandle()">文件提交</el-button>
			</el-form-item>
		</el-form>
		<el-table
			:data="dataList"
			border="border"
			v-loading="dataListLoading"
			cell-style="padding: 4px 0"
			style="width: 100%;"
			size="medium"
			@expand-change="solvenumber"
		>
		<el-table-column prop="index" header-align="center" align="center" width="100" label="序号">
				 <template #default="scope">
					<span>{{ (pageIndex - 1) * pageSize + scope.$index + 1 }}</span>
				</template> 
			</el-table-column>
		<el-table-column
				prop="key"
				header-align="center"
				align="center"
				label="文件内容"
				min-width="150"
			></el-table-column>
			<el-table-column header-align="center" align="center" width="150" label="操作" min-width="120">
				<template #default="scope">
					<el-button
						type="text"
						@click="workDownload(scope.row.key)"
					>
						下载  
					</el-button>
					<el-button
						type="text"
						size="medium"	
						@click="workPreview(scope.row.key)"
					>
						预览
					</el-button>
					<el-button
						type="text"
						size="medium"
						@click="deleteHandle(scope.row.key)"
					>
						删除
					</el-button>
				</template>
			</el-table-column>
		</el-table>
		<el-pagination
			@size-change="sizeChangeHandle"
			@current-change="currentChangeHandle"
			:current-page="pageIndex"
			:page-sizes="[10, 20, 50]"
			:page-size="pageSize"
			:total="totalCount"
			layout=" sizes, prev, pager, next, jumper"
		></el-pagination>
        <homework v-if="homeVisible" ref="home"  @refreshDataList="loadDataList" ></homework> 
	</div>
</template>





<script>
import dayjs from 'dayjs';
import LeaveAdd from './leave-add.vue';
import refuse from './approval_refuse.vue';
import approval from './approval.vue'
import homework from './homeHandle.vue'
export default {
	components: { LeaveAdd, refuse ,approval,homework},
	props:{
       taskId:null,
	},
	data: function() {
		return {
			taskId:'',
			pageIndex: 1,
			pageSize: 10,
			totalPage: 0,
			dataListLoading: false,
			dataListSelections: [],
            homeVisible: false,
			pathUrl:'',
			fileName:null,
			//查询条件
			dataForm: {
				name: null,
				deptId: null,
				date: null,
				text: null,
				status: null,
				fileName:null,
			},
			//页面表格数据
			dataList: [],
			deptList: [],
			
		};
	},
	methods: {

		solvenumber:function(row,expandedRows){
           console.log("row",row);
			console.log("expandedRows",expandedRows);
		},
		loadDeptList: function() {
			let that = this;
			that.$http('dept/searchAllDept', 'GET', null, true, function(resp) {
				that.deptList = resp.list;
			});
		},
		loadDataList: function() {
			let that = this;
			that.dataListLoading = true;
			//保存要提交的数据
			let data = {
				date: that.dataForm.date,
				page: that.pageIndex,
				length: that.pageSize,
			    key:that.key,
				pathUrl:that.pathUrl,
				fileName:that.fileName,
			};
			
			if (that.dataForm.date != null && that.dataForm.date != '') {
				data.date = dayjs(that.dataForm.date).format('YYYY-MM-DD');
			}
			that.$http('cos/searchCosFileByPage', 'POST', data, true, function(resp) {
				console.log("leresp",resp);
				 let page = resp.page;
				 let daa=resp.page.list[0].etag;
				 console.log("daa",daa);
				that.dataList = page.list;
				that.totalCount = page.totalCount;
				that.dataListLoading = false;
			
			});
		},
		workDownload:function(path){
		
			let data={
				path:path
			}
			this.$http('cos/downloadCosFile','POST',data,true,function(resp){
			   window.location = resp.url
			})
		},
		workPreview:function(path){
			let data={
				path:path
			}
			this.$http('cos/previewCosFile','POST',data,true,function(resp){
               window.location=resp.url
            })
		},
		sizeChangeHandle: function(val) {
			this.pageSize = val;
			this.pageIndex = 1;
			this.loadDataList();
		},
		currentChangeHandle: function(val) {
			this.pageIndex = val;
			this.loadDataList();
		},
		searchHandle: function() {
			this.$refs['dataForm'].validate(valid => {
				if (valid) {
					this.$refs['dataForm'].clearValidate();
					if (this.dataForm.name == '') {
						this.dataForm.name = null;
					}
					if (this.pageIndex != 1) {
						this.pageIndex = 1;
					}
					this.loadDataList();
				} else {
					return false;
				}
			});
		},
		addHandle: function() {
			this.addVisible = true;
			this.$nextTick(() => {
				this.$refs.add.init();
			});
		},
        workHandle: function(){
            this.homeVisible=true;
            this.$nextTick(()=>{
               this.$refs.home.init();
            })
        },
		deleteHandle: function(id) {
			let that = this;
			let pathes=[]
			pathes.push(id)
			console.log("scope.row",id)
			that.$confirm(`确定要删除选中的记录？`, '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
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
						that.loadDataList();
					} else {
						that.$message({
							message: '未能删除记录',
							type: 'warning',
							duration: 1200
						});
					}
				});
			});
		},
		pdfHandle:function(id){
			this.pdfVisible=true
			this.$nextTick(()=>{
				this.$refs.pdf.init(id)
			})
		}
	},
	created: function() {
		this.loadDeptList();
		this.loadDataList();
	}
};
</script>



<style lang="less" scoped=""></style>
