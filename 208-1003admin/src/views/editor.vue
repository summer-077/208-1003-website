<template>
    <div id="div1"></div>
 
</template>
  
<script>
import E from 'wangeditor'
export default{
    data(){

    },
    mounted(){
        const editor = new E('#div1')
        editor.create()

    },
    methods:{

    }

}

</script>

<style scoped>
#div1{
    margin-top: 50px;;
}
</style>