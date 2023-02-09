import { Item } from "semantic-ui-react"
import MyCommentsItem from "./MyCommentsItem"

const DUMMY_DATA = [
  {
    id: 1,
    cafeName: "스타벅스 역삼점",
    comment:
      "아 충전기 집에 놓고 옴 ㅡㅡㅡㅡㅡㅡㅡ흐으으으으ㅏㅡ아으ㅏ으ㅏ으ㅏ",
    postContent:
      "이름을 하나에 별빛이 아직 동경과 아이들의 시와 했던\
봅니다. 이름과, 사랑과 무엇인지 이름을 그러나 내일\
버리었습니다. 피어나듯이 보고, 어머니, 별 이름을 마리아\
내일 별 봅니다. 비둘기, 피어나듯이 나는 이네들은 걱정도\
가득 까닭입니다. 별 이제 같이 있습니다. 프랑시스 다하지\
남은 이름과, 있습니다. 이름을 하나에 별빛이 아직 동경과\
아이들의 시와 했던 봅니다. 이름과, 사랑과 무엇인지 이름을\
그러나 내일 버리었습니다. 피어나듯이 보고, 어머니, 별\
이름을 마리아 내일 별 봅니다. 비둘기, 피어나듯이 나는\
이네들은 걱정도 가득 까닭입니다. 별 이제 같이 있습니다.\
프랑시스 다하지 남은 이름과, 있습니다. 남은 이름과,\
있습니다. 이름을 하나에 별빛이 아직 동경과 아이들의 시와\
했던 봅니다. 이름과, 사랑과 무엇인지 이름을 그러나 내일\
버리었습니다. 피어나듯이 보고, 어머니, 별 이름을 마리아\
내일 별 봅니다. 비둘기, 피어나듯이 나는 이네들은 걱정도\
가득 까닭입니다. 별 이제 같이 있습니다. 프랑시스 다하지\
남은 이름과, 있습니다. 남은 이름과, 있습니다. 이름을\
하나에 별빛이 아직 동경과 아이들의 시와 했던 봅니다.\
이름과, 사랑과 무엇인지 이름을 그러나 내일 버리었습니다.\
피어나듯이 보고, 어머니, 별 이름을 마리아 내일 별 봅니다.\
비둘기, 피어나듯이 나는 이네들은 걱정도 가득 까닭입니다.\
별 이제 같이 있습니다. 프랑시스 다하지 남은 이름과,\
있습니다. 남은 이름과, 있습니다. 이름을 하나에 별빛이 아직\
동경과 아이들의 시와 했던 봅니다. 이름과, 사랑과 무엇인지\
이름을 그러나 내일 버리었습니다. 피어나듯이 보고, 어머니,\
별 이름을 마리아 내일 별 봅니다. 비둘기, 피어나듯이 나는\
이네들은 걱정도 가득 까닭입니다. 별 이제 같이 있습니다.\
프랑시스 다하지 남은 이름과, 있습니다.",
  },
  {
    id: 2,
    cafeName: "할리스 역삼점",
    comment: "오늘 시스템하기 싫지만 다시 할리스 왔다ㅜㅜ,, 집고가고파",
    postContent:
      "이름을 하나에 별빛이 아직 동경과 아이들의 시와 했던\
봅니다. 이름과, 사랑과 무엇인지 이름을 그러나 내일\
버리었습니다. 피어나듯이 보고, 어머니, 별 이름을 마리아\
내일 별 봅니다. 비둘기, 피어나듯이 나는 이네들은 걱정도\
가득 까닭입니다. 별 이제 같이 있습니다. 프랑시스 다하지\
남은 이름과, 있습니다. 이름을 하나에 별빛이 아직 동경과\
아이들의 시와 했던 봅니다. 이름과, 사랑과 무엇인지 이름을\
그러나 내일 버리었습니다. 피어나듯이 보고, 어머니, 별\
이름을 마리아 내일 별 봅니다. 비둘기, 피어나듯이 나는\
이네들은 걱정도 가득 까닭입니다. 별 이제 같이 있습니다.\
프랑시스 다하지 남은 이름과, 있습니다. 남은 이름과,\
있습니다. 이름을 하나에 별빛이 아직 동경과 아이들의 시와\
했던 봅니다. 이름과, 사랑과 무엇인지 이름을 그러나 내일\
버리었습니다. 피어나듯이 보고, 어머니, 별 이름을 마리아\
내일 별 봅니다. 비둘기, 피어나듯이 나는 이네들은 걱정도\
가득 까닭입니다. 별 이제 같이 있습니다. 프랑시스 다하지\
남은 이름과, 있습니다. 남은 이름과, 있습니다. 이름을\
하나에 별빛이 아직 동경과 아이들의 시와 했던 봅니다.\
이름과, 사랑과 무엇인지 이름을 그러나 내일 버리었습니다.\
피어나듯이 보고, 어머니, 별 이름을 마리아 내일 별 봅니다.\
비둘기, 피어나듯이 나는 이네들은 걱정도 가득 까닭입니다.\
별 이제 같이 있습니다. 프랑시스 다하지 남은 이름과,\
있습니다. 남은 이름과, 있습니다. 이름을 하나에 별빛이 아직\
동경과 아이들의 시와 했던 봅니다. 이름과, 사랑과 무엇인지\
이름을 그러나 내일 버리었습니다. 피어나듯이 보고, 어머니,\
별 이름을 마리아 내일 별 봅니다. 비둘기, 피어나듯이 나는\
이네들은 걱정도 가득 까닭입니다. 별 이제 같이 있습니다.\
프랑시스 다하지 남은 이름과, 있습니다.",
  },
]
const MyComments = () => {
  return (
    <>
      <Item.Group style={{ width: "100%" }} divided>
        {DUMMY_DATA.map((comment) => {
          return <MyCommentsItem comment={comment} key={comment.id}/>
        })}
      </Item.Group>
    </>
  )
}

export default MyComments