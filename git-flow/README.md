# git flow란
* git에서 사용되는 branch를 효율적으로 활용한 사례
* git에서 사용되는 branch를 효율적으로 사용하게 해주는 program


# branch
* 메인 branch

	`master` 실제 service되고 있는 branch, tag를 통해 version으로 관리되어진다.

	`develop` 개발되어지는 source를 가지고 있는 branch

* 보조 branch

	`feature` 기능을 개발하게 되는 branch

	`release` feature branch에서 개발된 기능들이 develop으로 모두 merge되고 master에 배포되기 전 test를 진행하며 bug를 fix하는 branch, master로 merge되어질 때 develop에도 merge되어져야한다.

	`hotfixes` master에서 service 중인 상태에서 발생한 bug를 fix하기 위한 branch, master에서 branch를 따오며 master로 바로 merge되어지고 develop branch에도 merge를 실행해줘야한다.

# git flow 자세한 설명
   `blog` https://ililil9482.tistory.com/117
