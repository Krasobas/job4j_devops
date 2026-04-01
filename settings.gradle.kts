rootProject.name = "DevOps"
include("DevOps.intergrationTest")
include("intergrationTest")
include("src:intergrationTest")
findProject(":src:intergrationTest")?.name = "intergrationTest"
