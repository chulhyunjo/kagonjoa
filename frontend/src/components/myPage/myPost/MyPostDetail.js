import { useState } from "react"
import { useDispatch } from "react-redux"
import {
  Button,
  Modal,
  Header,
  Card,
  Image,
  Grid,
  Comment,
  Form,
  Divider,
  Label,
} from "semantic-ui-react"
import { ScrollPanel } from "primereact/scrollpanel"
import CommentItem from "../../mainPage/CommentItem"
import ToggleButton from "../../common/ToggleButton"
import { postsActions } from "../../../store/posts"
import MyPostForm from "./MyPostForm"

const BRAND_LOGOS = {
  할리스: "hollys",
  폴바셋: "paulbasset",
  파스쿠찌: "pascucci",
  투썸플레이스: "twosome",
  토프레소: "topresso",
  텐퍼센트커피: "tenpercent",
  탐앤탐스: "tomntoms",
  컴포즈커피: "compose",
  커피에반하다: "coffeebanada",
  커피스미스: "coffeesmith",
  커피빈: "coffeebean",
  커피베이: "coffeebay",
  커피나무: "coffeenamu",
  카페베네: "caffeebene",
  카페띠아모: "caffetiamo",
  전광수: "jungwang",
  이디야커피: "edia",
  요거프레소: "yogerpresso",
  엔제리너스: "angelinus",
  스타벅스: "starbucks",
  스무디킹: "smoothy",
  셀렉토커피: "selecto",
  빽다방: "paiksdabang",
  베스킨라빈스: "baskin",
  메가커피: "megacoffee",
  매머드: "mammoth",
  드롭탑: "droptop",
  더벤티: "theventi",
  달콤커피: "dalkomm",
  나우커피: "nowcoffee",
  공차: "gongcha",
  개인카페: "selfcafe",
  바나프레소: "banapresso",
}

const MyPostDetail = (props) => {
  const dispatch = useDispatch()
  const [open, setOpen] = useState(false)
  const createdAt = props.post.createdAt.split('T')
  return (
    <Modal
      onClose={() => setOpen(false)}
      onOpen={() => setOpen(true)}
      open={open}
      size="large"
      trigger={<Button size="mini" color="green" style={{margin:0}}>자세히</Button>}
    >
      <Label
        color="orange"
        floating
        icon={{ name: "comment", size: "large", style: { margin: "0px" } }}
      />
      <Modal.Content>
        <div>
          <Grid>
            <Grid.Column mobile={16} tablet={8} computer={8}>
              <ScrollPanel style={{ width: "100%", height: "77.5vh" }}>
                <Card fluid style={{ boxShadow: "none" }}>
                  <Card.Content>
                    {props.post.imgUrlPath.length >0 && 
                    <Image
                    avatar
                    floated="left"
                    size="huge"
                    src={props.post.brandType ? require(`../../../assets/cafe_logos/${BRAND_LOGOS[props.post.brandType]}.png`) : ""}
                    />
                  }
                    <Card.Header>{props.post.writerNickname}</Card.Header>
                    <Card.Meta>{props.post.cafeName? props.post.cafeName : ''}</Card.Meta>
                    <Card.Meta textAlign="right">
                      {createdAt[0]} {createdAt[1]}
                    </Card.Meta>
                    {props.post.imgUrlPath.length > 0 && props.post.imgUrlPath.map((img,index)=>{
                      return <Image
                      key={index}
                      src={img}
                      wrapped
                      ui={true}
                      style={{ marginBlock: "0.5rem" }}
                    />
                    })}
                    <Card.Description
                      style={{ fontSize: "1.2rem", lineHeight: "1.8" }}
                      dangerouslySetInnerHTML={{ __html: props.post.content }}
                    >

                    </Card.Description>
                  </Card.Content>
                </Card>
              </ScrollPanel>
              <div style={{ display: "flex", marginTop: "1rem" }}>
              {sessionStorage.getItem("nickname") === props.post.writerNickname && (
                  <MyPostForm isEditing postToEdit={props.post} />
                )}
                {sessionStorage.getItem("nickname") === props.writer && (
                  <Button
                    fluid
                    toggle
                    // inverted
                    color="grey"
                    icon="delete"
                    content="삭제"
                    onClick={() => {
                      dispatch(postsActions.deletePost(props.post.id))
                      setOpen(false)
                    }}
                  ></Button>
                )}
                <ToggleButton
                  btnType="like"
                  fluid
                  inverted
                  content={props.post.postLikeCount}
                  likeHandler={props.likeHandler}
                />
              </div>
            </Grid.Column>
            <Grid.Column mobile={16} tablet={8} computer={8}>
              <Header>{`댓글 ${props.post.commentCount}`}</Header>
              <Divider />
              <ScrollPanel style={{ width: "100%", height: "70vh" }}>
                <Comment.Group>
                </Comment.Group>
              </ScrollPanel>
              <Divider />
              <Form reply>
                <Form.Input
                  fluid
                  placeholder="댓글을 입력하세요."
                  action={{
                    color: "brown",
                    icon: "paper plane",
                  }}
                  size="tiny"
                />
              </Form>
            </Grid.Column>
          </Grid>
        </div>
      </Modal.Content>
    </Modal>
  )
}

export default MyPostDetail