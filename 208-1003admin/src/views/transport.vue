<template>
	<div>
		<el-form :inline="true" :model="dataForm" :rules="dataRule" ref="dataForm">
			<el-form-item prop="name">
				<el-input
					v-model="dataForm.name"
					class="input"
					placeholder="姓名"
					size="medium"
					clearable="clearable"
				></el-input>
			</el-form-item>
			<el-form-item>
				<el-input
					v-model="dataForm.number"
					class="input"
					placeholder="快递单号"
					size="medium"
					clearable="clearable"
				>	
				</el-input>
			</el-form-item>
			<el-form-item>
				<el-input
					v-model="dataForm.origin"
					class="input"
					placeholder="出发地"
					size="medium"
					clearable="clearable"
				>	
				</el-input>
			</el-form-item>
			<el-form-item>
				<el-input
					v-model="dataForm.destination"
					class="input"
					placeholder="目的地"
					size="medium"
					clearable="clearable"
				>	
				</el-input>
			</el-form-item>
			<el-form-item>
				<el-button @click="searchHandle()" type="primary" size="medium">查询</el-button>
				<el-button type="danger" size="medium" @click.native="addHandle()">新增</el-button>
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
				prop="name"
				header-align="center"
				align="center"
				label="姓名"
				min-width="150"
			></el-table-column>
			<el-table-column
				prop="number"
				header-align="center"
				align="center"
				label="快递单号"
				min-width="150"
			></el-table-column>
			<el-table-column
				prop="createTime"
				header-align="center"
				align="center"
				label="创建时间"
				min-width="180"
			></el-table-column>
			<el-table-column
				prop="origin"
				header-align="center"
				align="center"
				label="出发地"
				min-width="180"
			></el-table-column>
			<el-table-column
				prop="destination"
				header-align="center"
				align="center"
				label="目的地"
				min-width="150"
			></el-table-column>
			<el-table-column
				prop="price"
				header-align="center"
				align="center"
				label="价格"
				min-width="150"
			></el-table-column>
		
			<el-table-column header-align="center" align="center" width="150" label="操作" min-width="120">
				<template #default="scope">
					<el-button
						type="text"
						size="medium"
						@click="updateHandle(scope.row.id)"
					>
						修改
					</el-button>
					<el-button
						type="text"
						size="medium"
						@click="deleteHandle(scope.row.id)"
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
			layout="sizes, prev, pager, next, jumper"
		></el-pagination>
		<transport-add v-if="addVisible" :taskId="taskId" ref="add" @refreshDataList="loadDataList"></transport-add>
	
	</div>
</template>

<script>
import TransportAdd from './transport_add.vue';
import approval from './approval.vue'

export default {
	components: { TransportAdd, approval},
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
			addVisible: false,
			pdfVisible: false,
			//查询条件
			dataForm: {
				date:null,
				name: null,
				number: null,
				origin: null,
				destination: null,
				price: null
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
				page:that.pageIndex,
				length:that.pageSize,
				name: that.dataForm.name,
				number: that.dataForm.number,
				origin: that.dataForm.origin,
				destination: that.dataForm.destination,
				price:that.dataForm.price,
			};
			that.$http('package/searchPackageByPage', 'POST', data, true, function(resp) {
				console.log("leresp",resp);
				let page = resp.page;
				that.dataList = page.list;
				that.totalCount = page.totalCount;
				that.dataListLoading = false;
			
			});
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
		deleteHandle: function(id) {
			let that = this;
			that.$confirm(`确定要删除选中的记录？`, '提示', {
				confirmButtonText: '确定',
				cancelButtonText: '取消',
				type: 'warning'
			}).then(() => {
				let data = {
					ids: [id]
				};
				that.$http('package/deletePackageByIds', 'POST', data, true, function(resp) {
					if (resp.rows > 0) {
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
		updateHandle: function(id) {
            this.addVisible = true;
            this.$nextTick(() => {
                this.$refs.add.init(id);
            });
        }
	},

	created: function() {
		this.loadDeptList();
		this.loadDataList();
	}
};
</script>

<style lang="less" scoped=""></style>
