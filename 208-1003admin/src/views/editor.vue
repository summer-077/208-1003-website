<template>
    <button @click="dianji">ceshi</button>
    <div id="div1"></div>
 
</template>
  
<script>
import E from 'wangeditor'
export default{
    data(){
        return{
            editor:null

        }
        
    },
    mounted(){

        this.editor = new E('#div1')
        this.editor.create()
      

    },
    methods:{
        dianji(){
            console.log("html",this.editor.txt.html())
        }

    }

}

</script>

<style scoped>
#div1{
    margin-top: 50px;;
}
</style>