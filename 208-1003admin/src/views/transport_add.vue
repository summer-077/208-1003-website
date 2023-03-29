<template>
    <div>
        <el-dialog ref="add" :title="!dataForm.id ? '新增' : '修改'" :close-on-click-modal="false" v-model="visible" width="450px">
            <el-form :model="dataForm" ref="dataForm" :rules="dataRule" label-width="80px">
                <el-form-item label="订单编号" prop="number">
                    <el-input
                        v-model="dataForm.number"
                        :autosize="{ minRows: 4, maxRows: 6 }"
                        clearable="clearable"
                    />
                </el-form-item>

                <el-form-item label="出发地" prop="origin">
                    <el-input
                        v-model="dataForm.origin"
                        :autosize="{ minRows: 4, maxRows: 6 }"
                        clearable="clearable"
                    />
                </el-form-item>

                <el-form-item label="目的地" prop="destination">
                    <el-input
                        v-model="dataForm.destination"
                        clearable="clearable"
                    />
                </el-form-item>

                <el-form-item label="价格" prop="price">
                    <el-input
                        v-model="dataForm.price"
                        :autosize="{ minRows: 4, maxRows: 6 }"
                        clearable="clearable"
                    />
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
    export default {
        data: function() {
            return {
                url: this.$baseUrl + 'cos/uploadCosFile?type=leaveinsert',
                picList: {},
                visible: false,
                files:{},
                dataForm: {
                    id:null,
                    number: null,
                    origin: null,
                    destination: null,
                    price: null,
                },
                dataRule: {
                    number: [{required: true, 
                              pattern:'(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
                              message: '订单编号错误' }],
                    origin: [{ required: true, message: '出发地不能为空' }],
                    destination: [{ required: true, message: '目的地不能为空' }],
                    price: [{ required: true,
                              pattern:'(^[1-9]([0-9]+)?(\.[0-9]{1,2})?$)|(^(0){1}$)|(^[0-9]\.[0-9]([0-9])?$)',
                              message: '价格错误' }],
                }
            };
        },
        methods: {
                init: function(id) {
                let that = this;
                that.dataForm.id = id || 0;
                that.visible = true;
                that.$nextTick(() => {
                    that.$refs['dataForm'].resetFields();
                    that.$refs['upload'].clearFiles();
                    if(id)
                    {
                        that.$http('package/searchPackageByPage', 'POST', { ids: id }, true, function(resp) {
                        that.dataForm.name = resp.name;
                        that.dataForm.number = resp.number;
                        that.dataForm.destination = resp.destination;
                        that.dataForm.origin = resp.origin;
                    });
                    }
                });
            },
            
            dataFormSubmit:function(file){
                let that=this
                let data={
                    id:that.dataForm.id,
                    number:that.dataForm.number,
                    origin:that.dataForm.origin,
                    destination:that.dataForm.destination,
                    price:that.dataForm.price
                    
                }
                this.$refs['dataForm'].validate(valid => {
                    if (valid) {
                        that.$http(`package/${!that.dataForm.id ? 'insert' : 'update'}`,"POST",data,true,function(resp){
                            console.log("resp",resp)
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
            
        },
        
    };
    </script>
    
    <style lang="less" scoped="scoped">
    .form-item {
        margin-bottom: 0 !important;
    }
    </style>
    